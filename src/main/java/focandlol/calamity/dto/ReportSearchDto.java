package focandlol.calamity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ReportSearchDto {

  private String query;

  private String jibunAddress;

  private String roadAddress;

  private String category;

  private LocalDate startDate;

  private LocalDate endDate;

}
