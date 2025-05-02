package focandlol.calamity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalamitySearchDto {
  private String message;

  private String category;

  private String sido;

  private String sigungu;

  private String createdAtFrom;

  private String createdAtTo;
}
