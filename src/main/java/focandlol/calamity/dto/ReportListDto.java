package focandlol.calamity.dto;

import focandlol.calamity.domain.ReportDocument;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportListDto {

  private Long id;

  private String title;

  private String content;

  private String category;

  private String roadAddress;

  private String jibunAddress;

  private LocalDateTime createdAt;

  public static ReportListDto from(ReportDocument reportDocument) {
    return ReportListDto.builder()
        .id(reportDocument.getId())
        .title(reportDocument.getTitle())
        .content(reportDocument.getContent())
        .category(reportDocument.getCategory())
        .roadAddress(reportDocument.getRoadAddress())
        .jibunAddress(reportDocument.getJibunAddress())
        .build();
  }
}
