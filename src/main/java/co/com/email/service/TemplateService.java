package co.com.email.service;

import co.com.email.domain.entities.MessageTemplate;
import co.com.email.repositories.MessageTemplateRepository;
import co.com.email.util.template.TemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TemplateService {

    private final MessageTemplateRepository repository;
    private final TemplateRenderer renderer;

    public TemplateService(MessageTemplateRepository repository,
                           TemplateRenderer renderer) {
        this.repository = repository;
        this.renderer = renderer;
    }

    public String build(String templateName, Map<String, Object> data) {

        MessageTemplate template = repository
                .findByTemplateNameAndActiveTrue(templateName)
                .orElseThrow(() -> new RuntimeException("Template no encontrado"));

        return renderer.render(template.getContent(), data);
    }

    public String getSubject(String templateName) {
        return repository
                .findByTemplateNameAndActiveTrue(templateName)
                .map(MessageTemplate::getEmailSubject)
                .orElse("Sin asunto");
    }
}
