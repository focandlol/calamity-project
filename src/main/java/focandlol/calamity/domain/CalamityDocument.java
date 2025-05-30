package focandlol.calamity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import focandlol.calamity.dto.CalamityMessageDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "calamity-read")
@Setting(settingPath = "/elasticsearch/calamity-settings.json")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalamityDocument {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;

  @Field(type = FieldType.Text, analyzer = "custom_nori")
  private String message;

  @Field(type = FieldType.Text, analyzer = "custom_nori")
  private String region;

  @Field(type = FieldType.Keyword)
  private Set<String> regionsSet;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "custom_nori"),
      otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
  private Set<String> sido;

  @Field(type = FieldType.Nested)
  private Set<Region> regions;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "custom_nori"),
      otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
  private String category;

  @Field(type = FieldType.Date)
  private String createdAt;

  @Field(type = FieldType.Date)
  private String registeredDate;

  @Field(type = FieldType.Date)
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
    if (raw == null || raw.isBlank()) {
      return null;
    }
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
