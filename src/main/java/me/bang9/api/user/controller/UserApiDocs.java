package me.bang9.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.bang9.api.global.api.Bang9Response;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface UserApiDocs {
    @Operation(
            summary = "이메일로 유저 생성",
            description = "이메일 정보로 회원가입 성공 후 유저 정보 생성합니다.",
            requestBody = @RequestBody(
                    description = "생성 정보",
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
                                              "password": "password123!",
                                              "nickname": "Poby",
                                              "name": "Sangmin Kim"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "유저 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Bang9Response.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력 정보가 유효하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<Bang9Response<UserResponse>> createUser(UserCreateRequest request);

    @Operation(
            summary = "모든 유저 정보 조회",
            description = "모든 유저 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Bang9Response.class)
                    )
            )
    })
    ResponseEntity<Bang9Response<List<UserResponse>>> getAllUsers();

    @Operation(
            summary = "유저 정보 조회",
            description = "유저의 고유 식별자(UUID)를 사용하여 유저 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Bang9Response.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<Bang9Response<UserResponse>> getUserById(
            @Parameter(
                    description = "유저id (UUID)",
                    required = true, example = "123e4567-e89b-12d3-a456-426614174000"
            ) UUID userId);

    @Operation(
            summary = "유저 정보 수정",
            description = "유저id에 해당하는 유저의 정보를 수정합니다.",
            requestBody = @RequestBody(
                    description = "수정된 유저 정보",
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
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Bang9Response.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력 정보가 유효하지 않음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "닉네임이 이미 존재함",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<Bang9Response<UserResponse>> updateUser(
            @Parameter(
                    description = "유저id (UUID)",
                    required = true, example = "123e4567-e89b-12d3-a456-426614174000"
            ) UUID userId,
            UserUpdateRequest request);

    @Operation(
            summary = "유저 삭제 (Soft Delete)",
            description = "유저를 삭제합니다. 이 작업은 유저를 완전히 삭제하지 않고, status값을 false로 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "유저 삭제 성공 (Soft Delete)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Bang9Response.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 삭제된 유저입니다.",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "유저를 찾을 수 없음",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<Bang9Response<Void>> softDeleteUser(
            @Parameter(
                    description = "유저id (UUID)",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000"
            ) UUID userId);
}
