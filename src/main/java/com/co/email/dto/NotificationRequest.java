package com.co.email.dto;

import lombok.Data;

@Data
public class NotificationRequest {
//Se podria eliminar
    private String email;
    private String subject;
    private String message;
}
