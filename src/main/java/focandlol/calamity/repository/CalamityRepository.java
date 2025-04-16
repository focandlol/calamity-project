package focandlol.calamity.repository;

import focandlol.calamity.dto.CalamityMessageDto;
import java.util.Optional;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CalamityRepository extends MongoRepository<CalamityMessageDto, String> {
  Optional<CalamityMessageDto> findFirstByOrderByModifiedDateDesc();
}
