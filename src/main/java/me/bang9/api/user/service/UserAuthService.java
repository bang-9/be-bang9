package me.bang9.api.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.auth.dto.UserInfoResponse;
import me.bang9.api.global.api.exception.Bang9Exception;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.model.UserRole;
import me.bang9.api.user.repository.UserJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static me.bang9.api.global.api.code.status.UserErrorStatus.DUPLICATE_EMAIL;
import static me.bang9.api.global.api.code.status.UserErrorStatus.USER_ACCOUNT_DISABLED;
import static me.bang9.api.global.api.code.status.UserErrorStatus.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService implements UserAuthUseCase {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with email: {}", request.email());

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Duplicate email attempted: {}", request.email());
            throw new Bang9Exception(DUPLICATE_EMAIL);
        }

        // 사용자 엔티티 생성
        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname());
        user.setRole(UserRole.USER);
        user.setProvider(request.provider());

        // 저장
        UserEntity savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return UserResponse.from(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");

        List<UserEntity> users = userRepository.findAll();

        // 활성 사용자만 필터링 (소프트 삭제된 사용자 제외)
        List<UserResponse> activeUsers = users.stream()
                .filter(user -> user.getStatus() == Boolean.TRUE)
                .map(UserResponse::from)
                .toList();

        log.info("Found {} active users", activeUsers.size());
        return activeUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.info("Fetching user by ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new Bang9Exception(USER_NOT_FOUND);
                });

        // 소프트 삭제된 사용자 체크
        if (user.getStatus() == Boolean.FALSE) {
            log.warn("Attempt to access deleted user with ID: {}", userId);
            throw new Bang9Exception(USER_NOT_FOUND);
        }

        log.info("User found: {}", user.getEmail());
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo() {
        log.debug("User info request");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        if (authentication == null || !authentication.isAuthenticated()) {
//            log.warn("Unauthorized access to user info");
//            throw new Bang9Exception(UNAUTHORIZED_ACCESS);
//        }

        String username = authentication.getName();

        // Find user entity
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found for user info: {}", username);
                    return new Bang9Exception(USER_NOT_FOUND);
                });

        // Check if user account is active
        if (!user.getStatus()) {
            log.warn("User info request for disabled user: {}", username);
            throw new Bang9Exception(USER_ACCOUNT_DISABLED);
        }

        // Create user info response
        UserInfoResponse userInfo = UserInfoResponse.from(user);

        log.debug("User info retrieved for: {}", username);
        return userInfo;
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
        log.info("Updating user with ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for update with ID: {}", userId);
                    return new Bang9Exception(USER_NOT_FOUND);
                });

        // 소프트 삭제된 사용자 체크
        if (user.getStatus() == Boolean.FALSE) {
            log.warn("Attempt to update deleted user with ID: {}", userId);
            throw new Bang9Exception(USER_NOT_FOUND);
        }

        // 닉네임 업데이트
        user.setNickname(request.nickname());

        UserEntity updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());

        return UserResponse.from(updatedUser);
    }

    @Override
    @Transactional
    public void softDeleteUser(UUID userId) {
        log.info("Soft deleting user with ID: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for deletion with ID: {}", userId);
                    return new Bang9Exception(USER_NOT_FOUND);
                });

        user.softDelete();
        userRepository.save(user);
        log.info("User soft deleted successfully: {}", userId);

    }
}