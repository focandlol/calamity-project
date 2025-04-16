package focandlol.calamity.controller;

import focandlol.calamity.service.CalamityService;
import focandlol.calamity.service.ElasticManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
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

  @PostMapping("/index")
  public void createCalamity() throws Exception {
    elasticManager.createIndex("calamity-" + LocalDate.now());
  }

  @PostMapping("/template")
  public void createTemplate(){
    elasticManager.createTemplate("calamity","calamity-*","read","write");
  }

  @PostMapping("/save")
  public void saveCalamity(){
    elasticManager.save();
  }
}
