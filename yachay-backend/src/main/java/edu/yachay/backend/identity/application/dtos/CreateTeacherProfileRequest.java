package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para crear o actualizar perfiles de docente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeacherProfileRequest {

    @NotNull(message = "ID de usuario es requerido")
    private String userId;

    @Size(max = 50)
    private String employeeId;

    @Size(max = 255)
    private String specialization;

    @NotNull(message = "Fecha de contratación es requerida")
    private LocalDate hireDate;

    @NotNull(message = "ID de escuela es requerido")
    private Integer schoolId;
}
