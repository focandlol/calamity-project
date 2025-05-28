package focandlol.calamity.controller;

import focandlol.calamity.domain.Report;
import focandlol.calamity.dto.CreateReportDto;
import focandlol.calamity.dto.ReportDto;
import focandlol.calamity.dto.UpdateReportDto;
import focandlol.calamity.service.ReportService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

  private final ReportService reportService;

  @GetMapping("{reportId}")
  public ResponseEntity<ReportDto> getReport(@PathVariable Long reportId){
    return ResponseEntity.ok(reportService.getReport(reportId));
  }

  @PostMapping
  public ResponseEntity<Void> createReport(@RequestBody CreateReportDto reportDto) {
    Long id = reportService.create(reportDto);
    URI uri = URI.create("/report/" + id);
    return ResponseEntity.created(uri).build();
  }

  @PutMapping("/{reportId}")
  public ResponseEntity<Void> updateReport(@PathVariable Long reportId,
      @RequestBody UpdateReportDto reportDto) {
    reportService.update(reportId, reportDto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{reportId}")
  public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
    reportService.delete(reportId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/title/auto_complete")
  public ResponseEntity<List<String>> getTitleAutoComplete(@RequestParam String query){
    return ResponseEntity.ok(reportService.titleAuto(query));
  }

}
