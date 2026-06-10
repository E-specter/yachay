package edu.yachay.backend.identity.infrastructure.adapters.inputs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.*;
import java.util.Map;

/**
 * DTO para respuestas de error.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}
