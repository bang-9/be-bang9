package me.bang9.api.auth.service;

import me.bang9.api.auth.dto.AuthResponse;
import me.bang9.api.auth.dto.LoginRequest;
import me.bang9.api.auth.dto.RefreshTokenRequest;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.res.UserResponse;

public interface AuthUseCase {

    /**
     * 사용자 회원가입
     * 새로운 사용자 계정을 생성합니다.
     *
     * @param request 회원가입 요청 (이메일, 비밀번호, 닉네임, 제공자)
     * @return 생성된 사용자 정보
     * @throws me.bang9.api.global.api.exception.Bang9Exception 이메일이 이미 존재하는 경우 (DUPLICATE_EMAIL)
     * @throws me.bang9.api.global.api.exception.Bang9Exception 유효성 검사 실패 시
     */
    UserResponse register(UserCreateRequest request);

    /**
     * 사용자 로그인 인증
     * 이메일과 비밀번호를 통한 사용자 인증을 수행하고 JWT 토큰을 발급합니다.
     *
     * @param request 로그인 요청 (이메일, 비밀번호)
     * @return JWT 토큰 및 사용자 정보를 포함한 인증 응답
     * @throws me.bang9.api.global.api.exception.Bang9Exception 인증 실패 시 (INVALID_CREDENTIALS)
     * @throws me.bang9.api.global.api.exception.Bang9Exception 사용자 계정이 비활성화된 경우 (USER_ACCOUNT_DISABLED)
     */
    AuthResponse login(LoginRequest request);

    /**
     * JWT 토큰 갱신
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
     *
     * @param request 토큰 갱신 요청 (리프레시 토큰)
     * @return 새로운 JWT 토큰 및 사용자 정보를 포함한 인증 응답
     * @throws me.bang9.api.global.api.exception.Bang9Exception 토큰이 유효하지 않은 경우 (INVALID_TOKEN)
     * @throws me.bang9.api.global.api.exception.Bang9Exception 토큰이 만료된 경우 (REFRESH_TOKEN_EXPIRED)
     * @throws me.bang9.api.global.api.exception.Bang9Exception 사용자를 찾을 수 없는 경우 (USER_NOT_FOUND)
     * @throws me.bang9.api.global.api.exception.Bang9Exception 사용자 계정이 비활성화된 경우 (USER_ACCOUNT_DISABLED)
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * 사용자 로그아웃
     * 현재 로그인된 사용자를 로그아웃하고 보안 컨텍스트를 정리합니다.
     * 향후 토큰 블랙리스트 기능이 추가될 예정입니다.
     *
     * @param request 로그아웃 요청 (액세스 토큰)
     */
    void logout();

}