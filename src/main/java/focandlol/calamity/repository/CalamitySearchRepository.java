package focandlol.calamity.repository;

import focandlol.calamity.dto.CalamityDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CalamitySearchRepository extends
    ElasticsearchRepository<CalamityDocument, String> {

  Page<CalamityDocument> findByRegionContaining(String keyword, Pageable pageable);
}
