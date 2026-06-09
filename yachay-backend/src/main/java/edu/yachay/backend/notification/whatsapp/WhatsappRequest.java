package edu.yachay.backend.notification.whatsapp;

import jakarta.validation.constraints.NotBlank;

public record WhatsappRequest(
        @NotBlank(message = "El numero destino es obligatorio.")
        String to,

        @NotBlank(message = "El mensaje es obligatorio.")
        String message
) {
}
