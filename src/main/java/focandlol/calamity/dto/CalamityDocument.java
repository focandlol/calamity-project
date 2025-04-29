package focandlol.calamity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalamityDocument {

  @Id
  private String id;

  private String message;

  private String region;

  private List<String> regionList;

  private List<String> sidoList;

  private List<String> sigunguList;

  private List<Region> regions;

  private String category;

  private String createdAt;

  private String registeredDate;

  private String modifiedDate;

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Region {
    private String sido;
    private String sigungu;
  }

  public static CalamityDocument from(CalamityMessageDto dto) {
    return CalamityDocument.builder()
        .id(dto.getId())
        .message(dto.getMessage())
        .region(dto.getRegion())
        .regionList(dto.getRegionList())
        .sidoList(dto.getSido())
        .sigunguList(dto.getSigungu())
        .regions(buildRegions(dto.getSido(), dto.getSigungu()))
        .category(dto.getCategory())
        .createdAt(formatIso8601(dto.getCreatedAt()))
        .registeredDate(formatIso8601(dto.getRegisteredDate()))
        .modifiedDate(formatIso8601(dto.getModifiedDate()))
        .build();
  }

  private static List<Region> buildRegions(List<String> sidoList, List<String> sigunguList) {
    if (sidoList == null || sigunguList == null || sidoList.size() != sigunguList.size()) {
      throw new IllegalArgumentException("sidoList와 sigunguList 길이가 다릅니다.");
    }

    List<Region> regions = new ArrayList<>();
    for (int i = 0; i < sidoList.size(); i++) {
      regions.add(new Region(sidoList.get(i), sigunguList.get(i)));
    }
    return regions;
  }

  private static String formatIso8601(String raw) {
    if (raw == null || raw.isBlank()) return null;
    DateTimeFormatter inputFormatter;
    if (raw.length() == 19) { // 2025/01/06 18:41:19
      inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    } else if (raw.length() == 29) { // 2025/01/06 18:41:19.123456789
      inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSSSSSSSS");
    } else {
      throw new IllegalArgumentException("Invalid date format: " + raw);
    }

    return LocalDateTime.parse(raw, inputFormatter)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
  }
}
