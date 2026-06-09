package edu.yachay.backend.document.dto;

public record DocumentResponse(
        boolean success,
        String message,
        String documentType,
        Long entityId,
        boolean providerConfigured
) {
}
