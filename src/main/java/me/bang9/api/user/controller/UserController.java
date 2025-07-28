package me.bang9.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bang9.api.global.api.ApiResponse;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.service.UserAuthUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static me.bang9.api.global.api.code.status.CommonSuccessStatus._CREATED;
import static me.bang9.api.global.api.code.status.CommonSuccessStatus._OK;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User authentication and management API")
public class UserController {

    private final UserAuthUseCase userAuthUseCase;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user account with the provided information",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User creation request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "User Creation Example",
                                    summary = "Example user creation request",
                                    value = """
                                            {
                                              "email": "user@example.com",
                                              "password": "password123",
                                              "nickname": "johndoe",
                                              "name": "John Doe"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.debug("Creating new user with email: {}", request.email());

        UserResponse response = userAuthUseCase.createUser(request);

        return ApiResponse.onSuccess(
                _CREATED.getCode(),
                _CREATED.getMessage(),
                response,
                HttpStatus.CREATED
        ).toResponseEntity();
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all active users in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.debug("Fetching all users");

        List<UserResponse> users = userAuthUseCase.getAllUsers();

        return ApiResponse.onSuccess(
                _OK.getCode(),
                _OK.getMessage(),
                users
        ).toResponseEntity();
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by their unique identifier"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User unique identifier", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        log.debug("Fetching user by ID: {}", userId);

        UserResponse response = userAuthUseCase.getUserById(userId);

        return ApiResponse.onSuccess(
                _OK.getCode(),
                _OK.getMessage(),
                response
        ).toResponseEntity();
    }

    @Operation(
            summary = "Update user",
            description = "Updates user information by their unique identifier",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequest.class),
                            examples = @ExampleObject(
                                    name = "User Update Example",
                                    summary = "Example user update request",
                                    value = """
                                            {
                                              "nickname": "newNickname",
                                              "name": "New Name"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Nickname already exists",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User unique identifier", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.debug("Updating user with ID: {}", userId);

        UserResponse response = userAuthUseCase.updateUser(userId, request);

        return ApiResponse.onSuccess(
                _OK.getCode(),
                _OK.getMessage(),
                response
        ).toResponseEntity();
    }

    @Operation(
            summary = "Soft delete user",
            description = "Soft deletes a user by marking them as inactive instead of permanently removing them"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User soft deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "User already deleted",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json")
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteUser(
            @Parameter(description = "User unique identifier", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID userId) {
        log.debug("Soft deleting user with ID: {}", userId);

        userAuthUseCase.softDeleteUser(userId);

        return ApiResponse.onSuccess().toResponseEntity();
    }
}
