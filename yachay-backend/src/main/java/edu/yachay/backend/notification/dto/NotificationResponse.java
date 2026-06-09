package edu.yachay.backend.notification.dto;

public record NotificationResponse(
        boolean success,
        String message
) {
}
