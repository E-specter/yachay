package edu.yachay.backend.admissions.dto;

import java.time.LocalDateTime;

public record AdmissionApplicationResponse(
        Long id,
        String postulante,
        String apoderado,
        String telefono,
        String correo,
        String nivel,
        String grado,
        String estado,
        String status,
        String observaciones,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaActualizacion
) {
}
