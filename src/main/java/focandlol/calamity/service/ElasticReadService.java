package focandlol.calamity.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.util.NamedValue;
import focandlol.calamity.domain.CalamityDocument;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ElasticReadService {

  private final ElasticsearchOperations elasticsearchOperations;

  public Map<String, Long> findAllWithAggregations() {
//    Query query = NativeQuery.builder()
//        //.withQuery(q -> q.range(r -> r.field("createdAt").gte(JsonData.of("now-10h/h"))))
//        .withAggregation("type_aggregation",
//            Aggregation.of(
//                a -> a.terms(t -> t.field("regionList.keyword").size(100))))
//        .withMaxResults(0)
//        .build();

    Query query = NativeQuery.builder()
        .withAggregation("시로_끝나는_지역_집계",
            Aggregation.of(a -> a
                .filter(f -> f
                    .wildcard(w -> w
                        .field("regionList.keyword")
                        .value("*시")
                    )
                )
                .aggregations("지역별_집계",
                    agg -> agg
                        .terms(t -> t
                            .field("regionList.keyword")
                            .size(100)
                            .order(Collections.singletonList(NamedValue.of("_count", SortOrder.Desc)))
                        )
                )
            )
        )
        .build();

    SearchHits<CalamityDocument> response = elasticsearchOperations.search(query,
        CalamityDocument.class);

    ElasticsearchAggregations aggregations = (ElasticsearchAggregations) response.getAggregations();
//    return aggregations.get("type_aggregation").aggregation().getAggregate()
//        .sterms()
//        .buckets()
//        .array()
//        .stream()
//        .collect(Collectors.toMap(bucket -> bucket.key()
//            .stringValue(), MultiBucketBase::docCount));
    return aggregations.get("시로_끝나는_지역_집계") // 바깥 필터 aggregation
        .aggregation()
        .getAggregate()
        .filter()
        .aggregations()
        .get("지역별_집계") // 내부 terms aggregation
        .sterms()
        .buckets()
        .array()
        .stream()
        .filter(bucket -> bucket.key().stringValue().endsWith("시"))
        //.sorted(Comparator.comparingLong(MultiBucketBase::docCount).reversed())
        .collect(Collectors.toMap(
            bucket -> bucket.key().stringValue(),
            stringTermsBucket -> stringTermsBucket.docCount(),
            (a, b) -> b,
            () -> new LinkedHashMap<String, Long>()
        ));

  }
}