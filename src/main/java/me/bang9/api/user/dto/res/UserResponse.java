package me.bang9.api.user.dto.res;

import me.bang9.api.user.entity.AgencyEntity;
import me.bang9.api.user.entity.UserEntity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String email,
        String nickname,
        String role,
        String provider,
        Set<String> agency
) {
    public static UserResponse from(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                user.getProvider().name(),
                user.getMemberAgencyList().stream()
                        .map(AgencyEntity::getName)
                        .collect(Collectors.toSet())
        );
    }
}
