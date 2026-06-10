package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import java.time.*;

/**
 * DTO para representar un Rol en las respuestas de la API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
