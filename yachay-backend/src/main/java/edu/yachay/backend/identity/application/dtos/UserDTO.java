package edu.yachay.backend.identity.application.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO para representar un Usuario en las respuestas de la API.
 * No incluye información sensible como contraseñas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String email;
    private String phone;
    private String displayName;
    private Boolean emailConfirmed;
    private Boolean phoneConfirmed;
    private LocalDateTime lastSignInAt;
    private Set<String> roleNames;
    private ProfileDTO profile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
