package focandlol.calamity.repository;

import focandlol.calamity.dto.CalamityMessageDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CalamityRepository extends MongoRepository<CalamityMessageDto, String> {

}
