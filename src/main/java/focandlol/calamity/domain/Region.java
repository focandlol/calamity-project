package focandlol.calamity.domain;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region {

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "custom_nori"),
      otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
  private String sido;

  @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "custom_nori"),
      otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword))
  private String sigungu;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Region region = (Region) o;
    return Objects.equals(getSido(), region.getSido()) && Objects.equals(
        getSigungu(), region.getSigungu());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSido(), getSigungu());
  }
}
