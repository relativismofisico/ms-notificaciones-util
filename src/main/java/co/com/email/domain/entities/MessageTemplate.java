package co.com.email.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "messages_templates")
@Data
public class MessageTemplate {

    @Id
    private Long id;

    private String templateName;

    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String emailSubject;

    private Boolean active;
}
