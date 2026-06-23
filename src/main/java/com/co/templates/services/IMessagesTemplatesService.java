package com.co.templates.services;

import com.co.templates.entities.MessagesTemplates;

import java.util.Optional;

public interface IMessagesTemplatesService {

    public Optional<MessagesTemplates> findById(Long id);
}
