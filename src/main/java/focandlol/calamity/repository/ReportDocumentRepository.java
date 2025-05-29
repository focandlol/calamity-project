package focandlol.calamity.repository;

import focandlol.calamity.domain.ReportDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReportDocumentRepository extends ElasticsearchRepository<ReportDocument, String> {

}
