package edu.yachay.backend.academic.dto;

import java.time.*;

public record CalendarEventResponse(
        Integer id,
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String eventType,
        String courseName,
        String sectionName,
        String audience,
        Integer courseId,
        String status
) {
}
