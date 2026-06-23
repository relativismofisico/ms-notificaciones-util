package com.co.templates.repositories;

import com.co.templates.entities.MessagesTemplates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMessagesTemplatesRepository extends JpaRepository<MessagesTemplates, Long> {
}
