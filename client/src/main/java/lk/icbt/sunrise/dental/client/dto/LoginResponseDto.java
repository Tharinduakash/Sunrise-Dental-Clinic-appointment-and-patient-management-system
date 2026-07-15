package lk.icbt.sunrise.dental.client.dto;

public record LoginResponseDto(String token, String username, String fullName, String role, long expiresInMillis) {
}
