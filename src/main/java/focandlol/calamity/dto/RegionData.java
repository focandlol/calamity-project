package focandlol.calamity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegionData {
  private final List<String> regionList;
  private final List<String> sidoList;
  private final List<String> sigunguList;
}
