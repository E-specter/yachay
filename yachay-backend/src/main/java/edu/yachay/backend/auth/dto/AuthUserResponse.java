package edu.yachay.backend.auth.dto;

import java.util.List;

public record AuthUserResponse(
        Long id,
        String nombres,
        String apellidos,
        String email,
        String role,
        String displayName,
        List<String> roles
) {
}
