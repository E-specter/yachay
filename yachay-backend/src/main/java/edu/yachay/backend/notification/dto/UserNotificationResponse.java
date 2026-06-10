package edu.yachay.backend.notification.dto;
import java.time.*;

public record UserNotificationResponse(
        Long id,
        String title,
        String body,
        String type,
        String linkUrl,
        Boolean read,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
