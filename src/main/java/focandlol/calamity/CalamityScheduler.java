package focandlol.calamity;

//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.BulkRequest;
//import co.elastic.clients.elasticsearch.core.BulkResponse;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import focandlol.calamity.common.RedissonLock;
//import focandlol.calamity.dto.CalamityDocument;
//import focandlol.calamity.dto.CalamityMessageDto;
//import focandlol.calamity.repository.CalamityRepository;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.bson.Document;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;

//@Service
//@RequiredArgsConstructor
//public class CalamityScheduler {
//
//  private final RestTemplate restTemplate = new RestTemplate();
//  private final CalamityRepository repository;
//  private final ElasticsearchClient elasticsearchClient;
//  private final ObjectMapper mapper;
//  private final RedissonClient redissonClient;
//
//  @Value("${calamity.api.base-url}")
//  private String baseUrl;
//
//  @Value("${calamity.api.service-key}")
//  private String serviceKey;
//
//  private static final String WRITE_ALIAS = "calamity-write";
//
//  //@Scheduled(fixedRate = 120000) // 1분마다 실행
//  @RedissonLock(prefix = "indexing", leaseTime = -1)
//  public void fetchAndIndex() {
//    System.out.println("scheduler start");
//    String startDate = getStartDate();
//    fetchSaveAndIndex(startDate, null); // 지역 전체
//    System.out.println("scheduler end");
//  }
//
//  public void fetchSaveAndIndex(String startDate, String region) {
//    int page = 1;
//    int rows = 100;
//
//    while (true) {
//      String json = fetchPage(page, rows, startDate, region);
//      JsonNode items = extractItems(json);
//
//      if (items == null || !items.isArray() || items.size() == 0) {
//        break;
//      }
//
//      List<CalamityDocument> esBatch = new ArrayList<>();
//
//      for (JsonNode item : items) {
//        String sn = item.path("SN").asText();
//        //if (repository.existsById(sn)) continue;
//
//        String rawRegion = item.path("RCPTN_RGN_NM").asText().trim();
//        List<String> regionList = parseRegionList(rawRegion);
//
//        CalamityMessageDto dto = CalamityMessageDto.from(item, regionList);
//
//        //CalamityMessageDto dto = CalamityMessageDto.from(item, regionData);
//
//        // MongoDB 저장
//        repository.save(dto);
//
//        // Elasticsearch 색인용 변환
//        esBatch.add(CalamityDocument.from(dto));
//      }
//
//      System.out.println("esBatch size: " + esBatch.size());
//      if (!esBatch.isEmpty()) {
//        bulkIndexToElasticsearch(esBatch);
//      }
//
//      page++;
//    }
//  }
//
//  private void bulkIndexToElasticsearch(List<CalamityDocument> documents) {
//    System.out.println("es 삽입 시도");
//    try {
//      BulkRequest.Builder builder = new BulkRequest.Builder();
//
//      for (CalamityDocument doc : documents) {
//        builder.operations(op -> op
//            .index(idx -> idx
//                .index(WRITE_ALIAS)
//                .id(doc.getId())
//                .document(doc)
//            )
//        );
//      }
//
//      System.out.println("es 삽입 직전");
//      BulkResponse response = elasticsearchClient.bulk(builder.build());
//      System.out.println("es 삽입 완료");
//      if (response.errors()) {
//        throw new RuntimeException("Elasticsearch 색인 중 일부 실패: " + response.items());
//      }
//    } catch (IOException e) {
//      throw new RuntimeException("Elasticsearch 색인 실패", e);
//    }
//  }
//
//  private String fetchPage(int pageNo, int numOfRows, String crtDt, String rgnNm) {
//    try {
//      StringBuilder url = new StringBuilder(baseUrl);
//      url.append("?serviceKey=").append(URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
//      url.append("&pageNo=").append(pageNo);
//      url.append("&numOfRows=").append(numOfRows);
//      url.append("&type=json");
//
//      if (crtDt != null) {
//        url.append("&crtDt=").append(crtDt);
//      }
//      if (rgnNm != null && !rgnNm.isBlank()) {
//        url.append("&rgnNm=").append(URLEncoder.encode(rgnNm, StandardCharsets.UTF_8));
//      }
//
//      ResponseEntity<String> response = restTemplate.getForEntity(url.toString(), String.class);
//      return response.getBody();
//    } catch (Exception e) {
//      throw new RuntimeException("API 호출 실패", e);
//    }
//  }
//
//  private JsonNode extractItems(String json) {
//    try {
//      JsonNode root = mapper.readTree(json);
//      return root.path("body");
//    } catch (Exception e) {
//      throw new RuntimeException("JSON 파싱 실패", e);
//    }
//  }
//
//  private List<String> parseRegionList(String region) {
//    if (region == null || region.isBlank()) return List.of();
//    return Arrays.asList(region.trim().split("\\s+"));
//  }
//
//  private String getStartDate() {
//    CalamityMessageDto noCalamityFound = repository.findFirstByOrderByModifiedDateDesc()
//        .orElseThrow(() -> new RuntimeException("No calamity found"));
//
//    String startDate = noCalamityFound.getRegisteredDate();
//    System.out.println("startDate: " + startDate);
//
//    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.n");
//    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//    LocalDateTime dateTime = LocalDateTime.parse(startDate, inputFormatter);
//
//    return dateTime.format(outputFormatter);
//  }
//}

