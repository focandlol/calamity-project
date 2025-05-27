package focandlol.calamity.dto;

import focandlol.calamity.domain.Report;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto {

  private Long id;

  private String title;

  private String content;

  private String category;

  private String roadAddress;

  private String jibunAddress;

  private LocalDateTime updatedAt;

  public static ReportDto from(Report report){
    return ReportDto.builder()
        .id(report.getId())
        .title(report.getTitle())
        .content(report.getContent())
        .category(report.getCategory())
        .roadAddress(report.getRoadAddress())
        .jibunAddress(report.getJibunAddress())
        .updatedAt(report.getUpdatedAt())
        .build();
  }

}
