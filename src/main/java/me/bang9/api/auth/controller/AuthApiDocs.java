package me.bang9.api.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.bang9.api.auth.dto.AuthResponse;
import me.bang9.api.auth.dto.LoginRequest;
import me.bang9.api.auth.dto.LogoutRequest;
import me.bang9.api.auth.dto.RefreshTokenRequest;
import me.bang9.api.global.api.Bang9Response;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication", description = "사용자 인증 및 토큰 관리 API")
public interface AuthApiDocs {

    @Operation(
            summary = "사용자 회원가입",
            description = "새로운 사용자 계정을 생성합니다. 이메일, 비밀번호, 닉네임 정보를 통해 회원가입을 진행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "409", description = "이메일이 이미 존재함"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    ResponseEntity<Bang9Response<UserResponse>> register(@Valid @RequestBody UserCreateRequest request);

    @Operation(
            summary = "사용자 로그인",
            description = "이메일과 비밀번호로 사용자 인증을 수행하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 잘못된 이메일 또는 비밀번호"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    ResponseEntity<Bang9Response<AuthResponse>> login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    ResponseEntity<Bang9Response<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request);

    @Operation(
            summary = "사용자 로그아웃",
            description = "현재 로그인된 사용자를 로그아웃합니다. (향후 토큰 블랙리스트 기능 추가 예정)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    ResponseEntity<Bang9Response<Void>> logout(@Valid @RequestBody LogoutRequest request);

}