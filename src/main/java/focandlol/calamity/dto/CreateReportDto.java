package focandlol.calamity.dto;

import focandlol.calamity.domain.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReportDto {

  @NotBlank
  @Size(min = 1, max = 20)
  private String title;

  @NotBlank
  private String content;

  @NotBlank
  private String category;

  @NotBlank
  private String roadAddress;

  @NotBlank
  private String jibunAddress;

  public Report to(){
    return Report.builder()
        .title(title)
        .content(content)
        .category(category)
        .roadAddress(roadAddress)
        .jibunAddress(jibunAddress)
        .build();
  }
}
