package focandlol.calamity;

//import focandlol.calamity.common.RedissonLock;
import focandlol.calamity.domain.CalamityDocument;
import focandlol.calamity.dto.CalamityMessageDto;
import focandlol.calamity.repository.CalamityRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.index.AliasData;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FullIndexingScheduler {

  private final ElasticsearchOperations elasticsearchOperations;
  private final CalamityRepository mongoRepository;

  private static final String READ_ALIAS = "calamity-read";
  private static final String WRITE_ALIAS = "calamity-write";
  private static final String NEW_INDEX = "calamity-" + LocalDate.now();

  @Scheduled(fixedRate = 120000)
  public void reindexAll() {
    log.info("full index start");
    createNewIndexWithAlias();
    switchWriteAlias();
    indexAllDocuments();
    switchReadAlias();
    log.info("full index com");
  }

  private void createNewIndexWithAlias() {
    log.info("create index");
    IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of(NEW_INDEX));
    if (!indexOps.exists()) {
      indexOps.create();
    }
    log.info("create index com");
  }

  private void switchWriteAlias() {
    Map<String, Set<AliasData>> existingAliases =
        elasticsearchOperations.indexOps(IndexCoordinates.of(WRITE_ALIAS)).getAliases();

    List<AliasAction> actions = new ArrayList<>();

    for (String oldIndex : existingAliases.keySet()) {
      actions.add(new AliasAction.Remove(
          AliasActionParameters.builder()
              .withIndices(oldIndex)
              .withAliases(WRITE_ALIAS)
              .build()
      ));
    }

    actions.add(new AliasAction.Add(
        AliasActionParameters.builder()
            .withIndices(NEW_INDEX)
            .withAliases(WRITE_ALIAS)
            .withIsWriteIndex(true)
            .build()
    ));

    AliasActions aliasActions = new AliasActions();
    aliasActions.add(actions.toArray(new AliasAction[0]));
    elasticsearchOperations.indexOps(IndexCoordinates.of(NEW_INDEX)).alias(aliasActions);
    log.info("switch write alias com");
  }

  private void indexAllDocuments() {
    List<CalamityMessageDto> dtoList = mongoRepository.findAll();
    List<CalamityDocument> documents = dtoList.stream()
        .map(CalamityDocument::from)
        .toList();

    elasticsearchOperations.save(documents, IndexCoordinates.of(WRITE_ALIAS));

    log.info("indexing com");
  }

  private void switchReadAlias() {
    Map<String, Set<AliasData>> existingAliases =
        elasticsearchOperations.indexOps(IndexCoordinates.of(READ_ALIAS)).getAliases();

    List<AliasAction> actions = new ArrayList<>();

    for (String oldIndex : existingAliases.keySet()) {
      actions.add(new AliasAction.Remove(
          AliasActionParameters.builder()
              .withIndices(oldIndex)
              .withAliases(READ_ALIAS)
              .build()
      ));
    }

    actions.add(new AliasAction.Add(
        AliasActionParameters.builder()
            .withIndices(NEW_INDEX)
            .withAliases(READ_ALIAS)
            .build()
    ));

    AliasActions aliasActions = new AliasActions();
    aliasActions.add(actions.toArray(new AliasAction[0]));

    elasticsearchOperations.indexOps(IndexCoordinates.of(NEW_INDEX)).alias(aliasActions);

    log.info("switch read alias com");
  }
}
