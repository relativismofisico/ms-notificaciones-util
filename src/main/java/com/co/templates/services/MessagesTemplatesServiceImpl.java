package com.co.templates.services;

import com.co.templates.entities.MessagesTemplates;
import com.co.templates.repositories.IMessagesTemplatesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesTemplatesServiceImpl implements IMessagesTemplatesService{

    private final IMessagesTemplatesRepository messagesTemplatesRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<MessagesTemplates> findById(Long id) {

        if (!messagesTemplatesRepository.existsById(id)){
            log.error("[MessagesTemplatesServiceImpl][findById][ms-notificaciones-util]\" + \" No se encuentra la plantilla en la BD");
            throw new RuntimeException("No se encuentra la plantilla en la BD");
        }

        return messagesTemplatesRepository.findById(id);
    }
}
