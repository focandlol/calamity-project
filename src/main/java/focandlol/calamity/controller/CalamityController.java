package focandlol.calamity.controller;

import focandlol.calamity.dto.CalamityDetailsDto;
import focandlol.calamity.dto.CalamityDocument;
import focandlol.calamity.dto.CalamityListDto;
import focandlol.calamity.dto.CalamitySearchDto;
import focandlol.calamity.service.CalamityService;
import focandlol.calamity.service.ElasticManager;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    elasticManager.createTemplate("calamity","calamity-*","calamity-read","calamity-write");
  }

  @GetMapping("/region_aggregation")
  public Map<String, Long> getRegionAggregation() throws IOException {
    return elasticManager.getRegionAggregation();
  }

  @GetMapping("/category_aggregation")
  public Map<String, Long> getCategoryAggregation() throws IOException {
    return elasticManager.getCategoryAggregation();
  }

  @GetMapping("/region")
  public List<CalamityListDto> getRegi(@RequestParam String region, Pageable pageable) throws IOException {
    return elasticManager.getRegionListData(region, pageable);
  }

  @GetMapping("/calamity/{id}")
  public CalamityDetailsDto getDetails(@PathVariable String id){
    return elasticManager.getCalamityDetails(id);
  }

  @GetMapping("/calamity")
  public Page<CalamityListDto> getCalamities(@ModelAttribute CalamitySearchDto dto, Pageable pageable)
      throws IOException {
    return elasticManager.search(dto, pageable);
  }
}
