package focandlol.calamity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import focandlol.calamity.dto.CalamityMessageDto;
import focandlol.calamity.dto.Region;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

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

  private Set<String> sido;

  private Set<Region> regions;

  private String category;

  private String createdAt;

  private String registeredDate;

  private String modifiedDate;

  public static CalamityDocument from(CalamityMessageDto dto) {
    return CalamityDocument.builder()
        .id(dto.getId())
        .message(dto.getMessage())
        .region(dto.getRegion())
        .sido(dto.getSido())
        .regions(dto.getRegions())
        .category(dto.getCategory())
        .createdAt(formatIso8601(dto.getCreatedAt()))
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
