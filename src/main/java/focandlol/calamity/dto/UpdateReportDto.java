package focandlol.calamity.dto;

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
public class UpdateReportDto {

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
}
