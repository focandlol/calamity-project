package focandlol.calamity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "calamity_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalamityMessageDto {

  @Id
  private String id;

  private String message;

  private String region;

  private List<String> regionList;

  private List<String> sido;

  private List<String> sigungu;

  private String createdAt;

  private String category;

  private String registeredDate;

  private String modifiedDate;

  public static CalamityMessageDto from(JsonNode item, RegionData regionData) {
    return CalamityMessageDto.builder()
        .id(item.path("SN").asText())
        .message(item.path("MSG_CN").asText())
        .region(item.path("RCPTN_RGN_NM").asText().trim())
        .regionList(regionData.getRegionList())
        .sido(regionData.getSidoList())
        .sigungu(regionData.getSigunguList())
        .createdAt(item.path("CRT_DT").asText())
        .category(item.path("DST_SE_NM").asText())
        .registeredDate(item.path("REG_YMD").asText())
        .modifiedDate(item.path("MDFCN_YMD").asText())
        .build();
  }

}
