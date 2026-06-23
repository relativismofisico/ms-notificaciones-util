package co.com.templates.repositories;

import co.com.templates.entities.MessagesTemplates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessagesTemplatesRepository extends JpaRepository<MessagesTemplates, Long> {
}
