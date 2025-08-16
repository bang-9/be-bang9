package me.bang9.api.auth.dto;

import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.model.UserRole;

import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        String email,
        String nickname,
        UserRole role
) {
    public static UserInfoResponse from(UserEntity user) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );
    }
}