package edu.yachay.backend.admissions.dto;

public record AdmissionDecisionRequest(
        String observaciones,
        String motivo,
        String nivel,
        String grado,
        String seccion,
        Boolean generarCredenciales,
        Boolean enviarCorreo
) {
}
