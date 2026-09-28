package com.guesswordgame.dto;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String token, String username, String role) {}
}
