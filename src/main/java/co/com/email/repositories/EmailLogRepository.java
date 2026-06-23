package co.com.email.repositories;

import co.com.email.domain.entities.EmailLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
}
