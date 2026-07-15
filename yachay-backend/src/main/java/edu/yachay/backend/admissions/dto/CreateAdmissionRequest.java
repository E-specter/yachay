package edu.yachay.backend.admissions.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CreateAdmissionRequest(
        @NotEmpty List<@Valid ApplicantRequest> postulantes,
        @NotNull @Valid GuardianRequest apoderado
) {
    public record ApplicantRequest(
            @NotBlank String apellidoPaterno,
            @NotBlank String apellidoMaterno,
            @NotBlank String nombres,
            @NotBlank String genero,
            @NotBlank String documentoTipo,
            @NotBlank String documentoNumero,
            @NotNull LocalDate fechaNacimiento,
            @NotBlank String viveCon,
            @NotBlank String colegioProcedencia,
            @NotBlank String lugarColegioProcedencia,
            String referenciaZonaColegio,
            @NotBlank String nivel,
            @NotBlank String grado
    ) {
    }

    public record GuardianRequest(
            @NotBlank String apellidoPaterno,
            @NotBlank String apellidoMaterno,
            @NotBlank String nombres,
            @NotBlank String genero,
            @NotBlank String documentoTipo,
            @NotBlank String documentoNumero,
            @NotBlank String parentesco,
            String telefono,
            @NotBlank String celular,
            @NotBlank @Email String correo,
            String profesion,
            String centroTrabajo,
            Boolean recibeNotificaciones
    ) {
    }
}
