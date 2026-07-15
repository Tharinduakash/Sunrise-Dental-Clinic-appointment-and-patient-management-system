package lk.icbt.sunrise.dental.dto;

public record LoginResponse(
        String token,
        String username,
        String fullName,
        String role,
        long expiresInMillis
) {
}
