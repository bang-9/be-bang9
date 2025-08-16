package me.bang9.api.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.auth.dto.AuthResponse;
import me.bang9.api.auth.dto.LoginRequest;
import me.bang9.api.auth.dto.LogoutRequest;
import me.bang9.api.auth.dto.RefreshTokenRequest;
import me.bang9.api.auth.service.AuthUseCase;
import me.bang9.api.global.api.Bang9Response;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.bang9.api.global.api.code.status.CommonSuccessStatus._CREATED;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "사용자 인증 및 토큰 관리 API")
public class AuthController implements AuthApiDocs {

    private final AuthUseCase authUseCase;

    @Override
    @PostMapping("/register")
    public ResponseEntity<Bang9Response<UserResponse>> register(@Valid @RequestBody UserCreateRequest request) {
        log.debug("User registration request for email: {}", request.email());

        UserResponse userResponse = authUseCase.register(request);

        return Bang9Response.onSuccess(
                _CREATED.getCode(),
                _CREATED.getMessage(),
                userResponse,
                HttpStatus.CREATED
        ).toResponseEntity();
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Bang9Response<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.debug("Login request for email: {}", request.email());

        AuthResponse authResponse = authUseCase.login(request);

        return Bang9Response.onSuccess(authResponse).toResponseEntity();
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<Bang9Response<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request");

        AuthResponse authResponse = authUseCase.refreshToken(request);

        return Bang9Response.onSuccess(authResponse).toResponseEntity();
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Bang9Response<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        log.debug("Logout request");

        authUseCase.logout(request);

        return Bang9Response.onSuccess().toResponseEntity();
    }


}