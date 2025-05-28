package focandlol.calamity.domain;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "report")
@Setting(settingPath = "/elasticsearch/report-settings.json")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReportDocument {

  @Id
  private Long id;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "report_title_analyzer"),
      otherFields = {
          @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori"),
      }
  )
  private String title;

  @Field(type = FieldType.Text, analyzer = "report_content_analyzer")
  private String content;

  @Field(type = FieldType.Text, analyzer = "report_address_analyzer")
  private String roadAddress;

  @Field(type = FieldType.Text, analyzer = "report_address_analyzer")
  private String jibunAddress;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "report_category_analyzer"),
    otherFields = {
      @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori")
    }
  )
  private String category;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
