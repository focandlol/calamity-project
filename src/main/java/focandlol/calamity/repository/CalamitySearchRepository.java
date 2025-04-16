package focandlol.calamity.repository;

import focandlol.calamity.dto.CalamityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CalamitySearchRepository extends ElasticsearchRepository<CalamityDocument, String> {

}
