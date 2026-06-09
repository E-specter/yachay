package edu.yachay.backend.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        @Email(message = "El destinatario debe ser un correo válido")
        String to,

        @NotBlank(message = "El asunto es obligatorio")
        String subject,

        @NotBlank(message = "El mensaje es obligatorio")
        String message
) {
}
