package co.com.templates.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "template_variables")
public class TemplateVariables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(name = "variable_name")
    private String variableName;
}
