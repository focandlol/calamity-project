package focandlol.calamity.dto;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalamityListDto {

  private String id;

  private String region;

  private String category;

  //private Set<Region> regions;

  private String modifiedDate;

  public static CalamityListDto from(CalamityDocument document){
    return CalamityListDto.builder()
        .id(document.getId())
        .region(document.getRegion())
        //.regions(document.getRegions())
        .category(document.getCategory())
        .modifiedDate(document.getModifiedDate())
        .build();
  }
}
