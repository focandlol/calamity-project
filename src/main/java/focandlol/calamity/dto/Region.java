package focandlol.calamity.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Region {
  private String sido;
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
