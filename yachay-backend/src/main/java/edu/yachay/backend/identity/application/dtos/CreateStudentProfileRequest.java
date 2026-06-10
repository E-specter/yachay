package edu.yachay.backend.identity.application.dtos;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.*;

/**
 * DTO para crear o actualizar perfiles de estudiante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudentProfileRequest {

    @NotNull(message = "ID de usuario es requerido")
    private String userId;

    @Size(max = 50)
    private String studentCode;

    @NotNull(message = "Grado es requerido")
    private Short gradeLevel;

    @Size(max = 20)
    private String section;

    @NotNull(message = "Fecha de inscripción es requerida")
    private LocalDate enrollmentDate;

    @NotNull(message = "ID de escuela es requerido")
    private Integer schoolId;
}
