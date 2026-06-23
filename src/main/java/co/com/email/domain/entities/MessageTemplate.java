package co.com.email.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
