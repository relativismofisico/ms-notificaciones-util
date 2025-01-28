package com.co.templates.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "messages_templates")
public class MessagesTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;

    private String description;

    private String content;

    @Column(name = "email_subject")
    private String emailSubject;
}
