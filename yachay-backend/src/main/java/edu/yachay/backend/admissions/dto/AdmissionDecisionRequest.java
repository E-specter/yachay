package edu.yachay.backend.admissions.dto;

public record AdmissionDecisionRequest(
        String observaciones,
        String motivo
) {
}
