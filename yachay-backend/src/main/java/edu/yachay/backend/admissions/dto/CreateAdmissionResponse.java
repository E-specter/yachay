package edu.yachay.backend.admissions.dto;

import java.time.LocalDateTime;

public record CreateAdmissionResponse(
        Long id,
        String estado,
        LocalDateTime fechaRegistro
) {
}
