package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear o actualizar roles.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

    @NotBlank(message = "Nombre de rol es requerido")
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 255)
    private String description;
}
