package me.bang9.api.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import me.bang9.api.global.validation.ValidNickname;

public record UserUpdateRequest(
        @NotBlank(message = "Nickname is required")
        @ValidNickname
        String nickname
) {
}