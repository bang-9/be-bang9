package me.bang9.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.auth.dto.AuthResponse;
import me.bang9.api.auth.dto.LoginRequest;
import me.bang9.api.auth.dto.RefreshTokenRequest;
import me.bang9.api.auth.dto.UserInfoResponse;
import me.bang9.api.global.api.exception.Bang9Exception;
import me.bang9.api.global.config.JwtProperties;
import me.bang9.api.global.security.jwt.JwtService;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.repository.UserJpaRepository;
import me.bang9.api.user.service.UserAuthUseCase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.bang9.api.global.api.code.status.UserErrorStatus.INVALID_CREDENTIALS;
import static me.bang9.api.global.api.code.status.UserErrorStatus.INVALID_TOKEN;
import static me.bang9.api.global.api.code.status.UserErrorStatus.REFRESH_TOKEN_EXPIRED;
import static me.bang9.api.global.api.code.status.UserErrorStatus.USER_ACCOUNT_DISABLED;
import static me.bang9.api.global.api.code.status.UserErrorStatus.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserJpaRepository userRepository;
    private final JwtProperties jwtProperties;
    private final UserAuthUseCase userAuthUseCase;

    @Override
    @Transactional
    public UserResponse register(UserCreateRequest request) {
        log.info("User registration attempt for email: {}", request.email());

        try {
            // Delegate user creation to existing UserAuthUseCase
            UserResponse userResponse = userAuthUseCase.createUser(request);

            log.info("User registered successfully: {}", request.email());
            return userResponse;

        } catch (Bang9Exception e) {
            log.warn("User registration failed for email: {} - {}", request.email(), e.getMessage());
            throw e; // Re-throw to preserve error codes
        } catch (Exception e) {
            log.error("Unexpected error during user registration for email: {}", request.email(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Find user entity for additional info
            UserEntity user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> {
                        log.warn("User not found during login: {}", request.email());
                        return new Bang9Exception(USER_NOT_FOUND);
                    });

            // Check if user account is active
            if (!user.getStatus()) {
                log.warn("Login attempt for disabled user: {}", request.email());
                throw new Bang9Exception(USER_ACCOUNT_DISABLED);
            }

            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

            // Create user info response
            UserInfoResponse userInfo = UserInfoResponse.from(user);

            // Create auth response
            AuthResponse authResponse = AuthResponse.of(
                    accessToken,
                    refreshToken,
                    jwtProperties.getAccessTokenExpiration(),
                    userInfo
            );

            log.info("User {} logged in successfully", request.email());
            return authResponse;

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for email: {}", request.email());
            throw new Bang9Exception(INVALID_CREDENTIALS);
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for email: {} - {}", request.email(), e.getMessage());
            throw new Bang9Exception(INVALID_CREDENTIALS);
        } catch (Bang9Exception e) {
            // Re-throw Bang9Exception to preserve error codes
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", request.email(), e);
            throw new Bang9Exception(INVALID_CREDENTIALS);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.debug("Token refresh request");

        try {
            String refreshToken = request.refreshToken();

            // Validate refresh token (check expiration)
            if (jwtService.isTokenExpired(refreshToken)) {
                throw new Bang9Exception(REFRESH_TOKEN_EXPIRED);
            }

            // Extract username from refresh token
            String username = jwtService.extractUsername(refreshToken);

            // Find user and validate
            UserEntity authenticatedUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
                        log.warn("User not found during token refresh: {}", username);
                        return new Bang9Exception(USER_NOT_FOUND);
                    });

            // Check if user account is active
            if (!authenticatedUser.getStatus()) {
                log.warn("Token refresh attempt for disabled user: {}", username);
                throw new Bang9Exception(USER_ACCOUNT_DISABLED);
            }

            // Generate new tokens
            UserDetails userDetails = User.builder()
                    .username(authenticatedUser.getEmail())
                    .password(authenticatedUser.getPassword())
                    .authorities(authenticatedUser.getRole().getValue())
                    .build();

            String newAccessToken = jwtService.generateAccessToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(username);

            // Create user info response
            UserInfoResponse userInfo = UserInfoResponse.from(authenticatedUser);

            // Create auth response
            AuthResponse authResponse = AuthResponse.of(
                    newAccessToken,
                    newRefreshToken,
                    jwtProperties.getAccessTokenExpiration(),
                    userInfo
            );

            log.info("Token refreshed successfully for user: {}", username);
            return authResponse;

        } catch (Bang9Exception e) {
            // Re-throw Bang9Exception to preserve error codes
            throw e;
        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw new Bang9Exception(INVALID_TOKEN);
        }
    }

    @Override
    public void logout() {
        log.debug("Logout request");

        try {
            // TODO: When token blacklist is implemented, add token to blacklist here
            // For now, we just clear the security context

            // Clear security context
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            SecurityContextHolder.clearContext();

            log.info("User {} logged out successfully", username);

        } catch (Exception e) {
            log.warn("Logout failed: {}", e.getMessage());
            // Don't throw exception for logout failures - just log them
        }
    }

}