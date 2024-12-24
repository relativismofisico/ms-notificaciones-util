package co.com.templates.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "variables_join_templates")
public class VariablesJoinTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variable_id", referencedColumnName = "id", nullable = false)
    private TemplateVariables templateVariables;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", referencedColumnName = "id", nullable = false)
    private MessagesTemplates messagesTemplates;
}
