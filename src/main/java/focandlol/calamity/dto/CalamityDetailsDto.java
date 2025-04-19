package focandlol.calamity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalamityDetailsDto {
  private String id;

  private String message;

  private String region;

  private List<String> regionList;

  private String createdAt;

  private String category;

  private String modifiedDate;

  public static CalamityDetailsDto from(CalamityDocument document){
    return CalamityDetailsDto.builder()
        .id(document.getId())
        .message(document.getMessage())
        .region(document.getRegion())
        .regionList(document.getRegionList())
        .createdAt(document.getCreatedAt())
        .category(document.getCategory())
        .modifiedDate(document.getModifiedDate())
        .build();
  }

}
