package focandlol.calamity.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.calamity.dto.CalamityMessageDto;
import focandlol.calamity.repository.CalamityRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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

        if (repository.existsById(sn)) {
          continue;
        }

        String rawRegion = item.path("RCPTN_RGN_NM").asText().trim();

        List<String> regionList = parseRegionList(rawRegion);

        repository.save(CalamityMessageDto.from(item, regionList));
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

  private List<String> parseRegionList(String region) {
    if (region == null || region.isBlank()) {
      return List.of();
    }
    return Arrays.asList(region.trim().split("\\s+"));
  }
}

