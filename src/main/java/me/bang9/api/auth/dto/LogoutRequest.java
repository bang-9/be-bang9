package me.bang9.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "액세스 토큰은 필수입니다")
        String accessToken
) {
}