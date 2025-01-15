package com.co.templates.repositories;

import com.co.templates.entities.TemplateVariables;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITemplateVariablesRepository extends JpaRepository<TemplateVariables, Long> {
}
