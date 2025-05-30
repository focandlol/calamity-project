package focandlol.calamity.dto;

import focandlol.calamity.domain.Region;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegionData {
  private final Set<String> sidoSet;
  private final Set<Region> regionSet;
}
