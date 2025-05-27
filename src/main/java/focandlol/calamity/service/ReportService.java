package focandlol.calamity.service;

import focandlol.calamity.domain.Report;
import focandlol.calamity.dto.CreateReportDto;
import focandlol.calamity.dto.ReportDto;
import focandlol.calamity.dto.UpdateReportDto;
import focandlol.calamity.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

  private final ReportRepository reportRepository;

  public Long create(CreateReportDto reportDto) {
    return reportRepository.save(reportDto.to()).getId();
  }

  @Transactional
  public void update(Long reportId, UpdateReportDto reportDto) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new RuntimeException("can't find report with id: " + reportId));

    report.setCategory(reportDto.getCategory());
    report.setTitle(reportDto.getTitle());
    report.setContent(reportDto.getContent());
    report.setJibunAddress(reportDto.getJibunAddress());
    report.setRoadAddress(reportDto.getRoadAddress());
  }


  public void delete(Long reportId) {
    reportRepository.deleteById(reportId);
  }

  public ReportDto getReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
        .orElseThrow(() -> new RuntimeException("can't find report with id: " + reportId));

    return ReportDto.from(report);
  }
}
