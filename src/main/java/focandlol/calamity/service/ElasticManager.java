package focandlol.calamity.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import co.elastic.clients.elasticsearch._types.analysis.CustomAnalyzer;
import co.elastic.clients.elasticsearch._types.analysis.EdgeNGramTokenFilter;
import co.elastic.clients.elasticsearch._types.analysis.NoriDecompoundMode;
import co.elastic.clients.elasticsearch._types.analysis.NoriTokenizer;
import co.elastic.clients.elasticsearch._types.analysis.TokenFilter;
import co.elastic.clients.elasticsearch._types.analysis.Tokenizer;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndexTemplateRequest;
import co.elastic.clients.elasticsearch.indices.put_index_template.IndexTemplateMapping;
import co.elastic.clients.util.NamedValue;
import focandlol.calamity.dto.CalamityDocument;
import focandlol.calamity.repository.CalamitySearchRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticManager {

  private final ElasticsearchClient client;
  private final CalamitySearchRepository calamityRepository;

  public void createTemplate(String templateName, String indexPattern, String readAlias,
      String writeAlias) {
    try {
      // Tokenizer 설정
      Map<String, Tokenizer> tokenizerMap = Map.of(
          "nori_autocomplete_tokenizer", new Tokenizer.Builder()
              .definition(NoriTokenizer.of(n -> n
                  .decompoundMode(NoriDecompoundMode.Mixed)
              )._toTokenizerDefinition())
              .build()
      );

      // Filter 설정
      Map<String, TokenFilter> filterMap = Map.of(
          "edge_ngram_filter", new TokenFilter.Builder()
              .definition(EdgeNGramTokenFilter.of(e -> e
                  .minGram(1)
                  .maxGram(20)
              )._toTokenFilterDefinition())
              .build()
      );

      // Analyzer 설정
      Map<String, Analyzer> analyzerMap = Map.of(
          "nori_autocomplete", new Analyzer.Builder()
              .custom(new CustomAnalyzer.Builder()
                  .tokenizer("nori_autocomplete_tokenizer")
                  .filter("lowercase", "edge_ngram_filter")
                  .build())
              .build(),
          "nori_search", new Analyzer.Builder()
              .custom(new CustomAnalyzer.Builder()
                  .tokenizer("nori_tokenizer")
                  .filter("lowercase")
                  .build())
              .build()
      );

      IndexSettings settings = new IndexSettings.Builder()
          .analysis(a -> a
              .tokenizer(tokenizerMap)
              .filter(filterMap)
              .analyzer(analyzerMap)
          )
          .build();

      // Mappings
      Map<String, Property> props = new HashMap<>();
      props.put("message", new Property.Builder()
          .text(t -> t.analyzer("nori_autocomplete").searchAnalyzer("nori_search"))
          .build());
      props.put("region", new Property.Builder()
          .text(t -> t.analyzer("nori_autocomplete").searchAnalyzer("nori_search"))
          .build());
      props.put("regionList", new Property.Builder()
          .text(t -> t.analyzer("nori_autocomplete").searchAnalyzer("nori_search")
              .fields("keyword", f -> f.keyword(k -> k)))
          .build());
      props.put("category", new Property.Builder()
          .text(t -> t.analyzer("nori_autocomplete").searchAnalyzer("nori_search")
              .fields("keyword", f -> f.keyword(k -> k)))
          .build());
      props.put("createdAt", new Property.Builder().date(d -> d).build());
      props.put("registeredDate", new Property.Builder().date(d -> d).build());
      props.put("modifiedDate", new Property.Builder().date(d -> d).build());

      TypeMapping mapping = new TypeMapping.Builder()
          .properties(props)
          .build();

      // Alias 설정
      Map<String, Alias> aliasMap = new HashMap<>();
      aliasMap.put(readAlias, Alias.of(a -> a));
      aliasMap.put(writeAlias, Alias.of(a -> a.isWriteIndex(true)));

      IndexTemplateMapping templateMapping = new IndexTemplateMapping.Builder()
          .settings(settings)
          .mappings(mapping)
          .build();

      // 템플릿 존재 여부 확인
      boolean exists = client.indices()
          .existsIndexTemplate(e -> e.name(templateName))
          .value();

      if (!exists) {
        PutIndexTemplateRequest putRequest = new PutIndexTemplateRequest.Builder()
            .name(templateName)
            .indexPatterns(indexPattern)
            .template(templateMapping)
            .build();

        boolean acknowledged = client.indices().putIndexTemplate(putRequest).acknowledged();
        System.out.println("Template created: " + acknowledged);
      } else {
        System.out.println("Template already exists: " + templateName);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void createIndex(String indexName) throws IOException {

    // 인덱스 생성 요청
    CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
        .index(indexName)  // 인덱스 이름
        .build();

    // 인덱스 생성
    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest);
    if (createIndexResponse.acknowledged()) {
      System.out.println("Index created successfully!");
    } else {
      System.out.println("Index creation failed!");
    }
  }

  public void save() {
    calamityRepository.save(CalamityDocument.builder()
        .id("12345611")
        .region("dddddd")
        .build());
  }

  public void add() throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("your-index-name")
            .query(q -> q.matchAll(m -> m))
            .aggregations("매출_카테고리별", a -> a
                .terms(t -> t.field("category.keyword"))
                .aggregations("총_매출", sa -> sa.sum(s -> s.field("amount")))
            ),
        Void.class
    );

    Map<String, Aggregate> aggregations = response.aggregations();
    StringTermsAggregate categoryAgg = aggregations.get("매출_카테고리별").sterms();

    for (StringTermsBucket bucket : categoryAgg.buckets().array()) {
      String category = bucket.key().stringValue();
      double totalAmount = bucket.aggregations()
          .get("총_매출")
          .sum()
          .value();

      System.out.println("카테고리: " + category + ", 총 매출: " + totalAmount);
    }
  }

  public Map<String, Long> getRegionAggregation() throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-write")
            .aggregations("시로_끝나는_지역_집계", a -> a
                .filter(f -> f
                    .wildcard(w -> w
                        .field("regionList.keyword")
                        .value("*시")
                    )
                )
                .aggregations("지역별_집계", t -> t
                    .terms(term -> term
                        .field("regionList.keyword")
                        .size(100)
                        .order(List.of(NamedValue.of("_count", SortOrder.Desc)))
                    )
                )
            ),
        Void.class
    );

    return response.aggregations()
        .get("시로_끝나는_지역_집계")
        .filter()
        .aggregations()
        .get("지역별_집계")
        .sterms()
        .buckets()
        .array()
        .stream()
        .filter(bucket -> bucket.key().stringValue().endsWith("시"))
        .collect(Collectors.toMap(
            bucket -> bucket.key().stringValue(),
            StringTermsBucket::docCount,
            (a, b) -> b,
            LinkedHashMap::new
        ));
  }

  public Map<String, Long> getCategoryAggregation() throws IOException {
    SearchResponse<Void> response = client.search(sr -> sr
            .index("calamity-write")
            .aggregations("카테고리_집계", t -> t
                .terms(term -> term
                    .field("category.keyword")
                    .size(100)
                    .order(List.of(NamedValue.of("_count", SortOrder.Desc))))
            )
        , Void.class);

    return response.aggregations().get("카테고리_집계")
        .sterms()
        .buckets()
        .array()
        .stream()
        .collect(Collectors.toMap(
            bucket -> bucket.key().stringValue(),
            StringTermsBucket::docCount,
            (a,b) -> b,
            LinkedHashMap::new
        ));
  }


}
