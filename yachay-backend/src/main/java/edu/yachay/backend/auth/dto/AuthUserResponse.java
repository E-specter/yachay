package edu.yachay.backend.auth.dto;

public record AuthUserResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String role
) {
}
