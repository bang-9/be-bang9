package me.bang9.api.user.dto.req;

import jakarta.validation.constraints.Email;
import me.bang9.api.user.model.Provider;

public record UserCreateRequest(
        @Email
        String email,
        
        String password,
        String nickname,
        Provider provider
) {
}
