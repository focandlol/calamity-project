package focandlol.calamity.controller;

import focandlol.calamity.dto.CalamityDocument;
import focandlol.calamity.service.CalamityService;
import focandlol.calamity.service.ElasticManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalamityController {

  private final CalamityService calamityService;
  private final ElasticManager elasticManager;

  @PostMapping("/calamity")
  public void getCalamity() {
    calamityService.fetchAndSave("20250101", null);
  }

  @PostMapping("/_doc")
  public void createCalamity() throws Exception {
    elasticManager.createIndex("calamity-" + LocalDate.now());
  }

  @PostMapping("/_template")
  public void createTemplate(){
    elasticManager.createTemplate("calamity","calamity-*","read","write");
  }

  @GetMapping("/region_aggregation")
  public Map<String, Long> getRegionAggregation() throws IOException {
    return elasticManager.getRegionAggregation();
  }

  @GetMapping("/category_aggregation")
  public Map<String, Long> getCategoryAggregation() throws IOException {
    return elasticManager.getCategoryAggregation();
  }
}
