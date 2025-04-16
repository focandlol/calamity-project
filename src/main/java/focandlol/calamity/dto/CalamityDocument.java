package focandlol.calamity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Highlight;

@Document(indexName = "calamity-read")
@TypeAlias("")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalamityDocument {

  @Id
  private String id;

  private String message;

  private String region;

  private List<String> regionList;

  private String createdAt;

  private String category;

  private String registeredDate;

  private String modifiedDate;

  public static CalamityDocument from(CalamityMessageDto dto) {
    return CalamityDocument.builder()
        .id(dto.getId())
        .message(dto.getMessage())
        .region(dto.getRegion())
        .regionList(dto.getRegionList())
        .createdAt(dto.getCreatedAt())
        .category(dto.getCategory())
        .registeredDate(dto.getRegisteredDate())
        .modifiedDate(dto.getModifiedDate())
        .build();
  }


}
