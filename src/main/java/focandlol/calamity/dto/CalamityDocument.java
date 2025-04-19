package focandlol.calamity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
        .createdAt(formatIso8601(dto.getCreatedAt()))
        .category(dto.getCategory())
        .registeredDate(formatIso8601(dto.getRegisteredDate()))
        .modifiedDate(formatIso8601(dto.getModifiedDate()))
        .build();
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
