package edu.yachay.backend.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.*;

public record CreateCalendarEventRequest(
        @NotBlank String titulo,
        String descripcion,
        @NotNull LocalDateTime fechaInicio,
        @NotNull LocalDateTime fechaFin,
        @NotBlank String tipo,
        Integer cursoId,
        String seccion,
        String publicoObjetivo
) {
}
