package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear o actualizar perfiles de apoderado/tutor.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGuardianProfileRequest {

    @NotNull(message = "ID de usuario es requerido")
    private String userId;

    @Size(max = 50)
    private String relationship;

    @Size(max = 100)
    private String occupation;
}
