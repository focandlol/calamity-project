package focandlol.calamity.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  private Set<String> sido;

  private Set<Region> regions;

  private String createdAt;

  private String category;

  private String registeredDate;

  private String modifiedDate;

  public static CalamityMessageDto from(JsonNode item, RegionData regionData) {
    return CalamityMessageDto.builder()
        .id(item.path("SN").asText())
        .message(item.path("MSG_CN").asText())
        .region(item.path("RCPTN_RGN_NM").asText().trim())
        .sido(regionData.getSidoSet())
        .regions(regionData.getRegionSet())
        .createdAt(item.path("CRT_DT").asText())
        .category(item.path("DST_SE_NM").asText())
        .registeredDate(item.path("REG_YMD").asText())
        .modifiedDate(item.path("MDFCN_YMD").asText())
        .build();
  }

}
