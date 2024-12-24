package co.com.templates.repositories;

import co.com.templates.entities.TemplateVariables;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITemplateVariablesRepository extends JpaRepository<TemplateVariables, Long> {

}
