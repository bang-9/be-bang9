package me.bang9.api.user.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.bang9.api.global.validation.ValidNickname;
import me.bang9.api.global.validation.ValidPassword;
import me.bang9.api.user.model.Provider;

public record UserCreateRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is required")
        @ValidPassword
        String password,
        
        @NotBlank(message = "Nickname is required")
        @ValidNickname
        String nickname,
        
        @NotNull(message = "Provider is required")
        Provider provider
) {
}
