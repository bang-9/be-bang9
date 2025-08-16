package me.bang9.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        String refreshToken
) {
}