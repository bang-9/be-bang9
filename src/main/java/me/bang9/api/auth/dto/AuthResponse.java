package me.bang9.api.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserInfoResponse userInfo
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn, UserInfoResponse userInfo) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, userInfo);
    }
}