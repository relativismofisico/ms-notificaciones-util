package co.com.email.repositories;

import co.com.email.domain.entities.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    Optional<MessageTemplate> findByTemplateName(String templateName);
    Optional<MessageTemplate> findByTemplateNameAndActiveTrue(String templateName);


}
