package co.com.email.repositories;

import co.com.email.domain.entities.OutboxEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OutboxEventRepository extends MongoRepository<OutboxEventEntity, String> {
    List<OutboxEventEntity> findTop50ByEnviadoFalseOrderByFechaCreacionAsc();
}
