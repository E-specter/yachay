package edu.yachay.backend.auth.dto;

public record LoginResponse(
        String token,
        AuthUserResponse user
) {
}
