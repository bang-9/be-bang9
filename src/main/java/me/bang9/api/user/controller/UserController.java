package me.bang9.api.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.auth.dto.UserInfoResponse;
import me.bang9.api.global.api.Bang9Response;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.service.UserAuthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User authentication and management API")
public class UserController implements UserApiDocs {

    private final UserAuthUseCase userAuthUseCase;

    @Override
    @GetMapping
    public ResponseEntity<Bang9Response<List<UserResponse>>> getAllUsers() {
        log.debug("Fetching all users");

        List<UserResponse> users = userAuthUseCase.getAllUsers();

        return Bang9Response.onSuccess(users).toResponseEntity();
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<Bang9Response<UserResponse>> getUserById(@PathVariable UUID userId) {
        log.debug("Fetching user by ID: {}", userId);

        UserResponse response = userAuthUseCase.getUserById(userId);

        return Bang9Response.onSuccess(response).toResponseEntity();
    }

    @Override
    @GetMapping("/info")
    public ResponseEntity<Bang9Response<UserInfoResponse>> getUserInfo() {
        log.debug("User info request");

        UserInfoResponse userInfo = userAuthUseCase.getUserInfo();

        return Bang9Response.onSuccess(userInfo).toResponseEntity();
    }

    @PatchMapping("/{userId}")
    @Override
    public ResponseEntity<Bang9Response<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.debug("Updating user with ID: {}", userId);

        UserResponse response = userAuthUseCase.updateUser(userId, request);

        return Bang9Response.onSuccess(response).toResponseEntity();
    }

    @DeleteMapping("/{userId}")
    @Override
    public ResponseEntity<Bang9Response<Void>> softDeleteUser(
            @PathVariable UUID userId) {
        log.debug("Soft deleting user with ID: {}", userId);

        userAuthUseCase.softDeleteUser(userId);

        return Bang9Response.onSuccess().toResponseEntity();
    }
}
