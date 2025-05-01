package focandlol.calamity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.calamity.dto.CalamityMessageDto;
import focandlol.calamity.dto.Region;
import focandlol.calamity.dto.RegionData;
import focandlol.calamity.repository.CalamityRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CalamityService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final CalamityRepository repository;
  private final ObjectMapper mapper;

  @Value("${calamity.api.base-url}")
  private String baseUrl;

  @Value("${calamity.api.service-key}")
  private String serviceKey;

  public void fetchAndSave(String startDate, String region) {
    int page = 1;
    int rows = 100;

    while (true) {
      String json = fetchPage(page, rows, startDate, region);
      JsonNode items = extractItems(json);

      if (items == null || !items.isArray() || items.size() == 0) {
        break;
      }

      for (JsonNode item : items) {
        String sn = item.path("SN").asText();

//        if (repository.existsById(sn)) {
//          continue;
//        }

        String rawRegion = item.path("RCPTN_RGN_NM").asText().trim();

        RegionData regionData = parseRegionLists(rawRegion);

        repository.save(CalamityMessageDto.from(item, regionData));
      }

      page++;
    }
  }

  private String fetchPage(int pageNo, int numOfRows, String crtDt, String rgnNm) {
    try {
      StringBuilder url = new StringBuilder(baseUrl);
      url.append("?serviceKey=").append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
      url.append("&pageNo=").append(pageNo);
      url.append("&numOfRows=").append(numOfRows);
      url.append("&type=json");

      if (crtDt != null) {
        url.append("&crtDt=").append(crtDt);
      }
      if (rgnNm != null && !rgnNm.isBlank()) {
        url.append("&rgnNm=").append(URLEncoder.encode(rgnNm, StandardCharsets.UTF_8));
      }

      ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
      return response.getBody();
    } catch (Exception e) {
      throw new RuntimeException("API 호출 실패", e);
    }
  }

  private JsonNode extractItems(String json) {
    try {
      JsonNode root = mapper.readTree(json);
      return root.path("body");
    } catch (Exception e) {
      throw new RuntimeException("JSON 파싱 실패", e);
    }
  }

  private RegionData parseRegionLists(String region) {
    if (region == null || region.isBlank()) {
      return new RegionData(new HashSet<>(), new HashSet<>());
    }

    Set<Region> regionSet = new HashSet<>();
    Set<String> sidoSet = new HashSet<>();

    String[] regions = region.split(",");

    for (String r : regions) {
      r = r.trim();
      if (r.isEmpty()) continue;

      String[] parts = r.split("\\s+");

      String sido = "시도 정보 없음";
      String sigungu = "전체";

      if (parts.length >= 1) {
        String p0 = parts[0];

        if (p0.endsWith("도") || p0.endsWith("특별시") || p0.endsWith("광역시") || p0.endsWith("자치시")) {
          sido = p0;

          if (parts.length >= 2) {
            String p1 = parts[1];
            if (p1.endsWith("시") || p1.endsWith("군") || p1.endsWith("구")) {
              sigungu = p1;
            }
          }
        } else {
          // p0이 시군구일 경우
          sigungu = p0;
        }
      }

      sidoSet.add(sido);
      regionSet.add(new Region(sido, sigungu));
    }

    return new RegionData(
        sidoSet,
        regionSet
    );
  }
}

