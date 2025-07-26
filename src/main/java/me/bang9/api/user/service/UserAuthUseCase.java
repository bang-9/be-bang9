package me.bang9.api.user.service;

import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserAuthUseCase {
    
    /**
     * EMAIL 제공자를 통한 새 사용자 생성
     * @param request 이메일, 비밀번호, 닉네임, 제공자를 포함한 사용자 생성 요청
     * @return 생성된 사용자의 응답 데이터
     * @throws IllegalArgumentException 이메일이 이미 존재하거나 유효성 검사 실패 시
     */
    UserResponse createUser(UserCreateRequest request);
    
    /**
     * 시스템의 모든 사용자 조회
     * @return 모든 사용자의 응답 데이터 목록
     */
    List<UserResponse> getAllUsers();
    
    /**
     * ID로 특정 사용자 조회
     * @param userId 조회할 사용자의 UUID
     * @return 요청된 사용자의 응답 데이터
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    UserResponse getUserById(UUID userId);
    
    /**
     * 사용자 정보 수정 (현재는 닉네임만 지원)
     * @param userId 수정할 사용자의 UUID
     * @param request 새로운 닉네임을 포함한 수정 요청
     * @return 수정된 사용자의 응답 데이터
     * @throws IllegalArgumentException 사용자를 찾을 수 없거나 닉네임 유효성 검사 실패 시
     */
    UserResponse updateUser(UUID userId, UserUpdateRequest request);
    
    /**
     * 삭제 플래그를 설정하여 사용자 소프트 삭제
     * @param userId 삭제할 사용자의 UUID
     * @throws IllegalArgumentException 사용자를 찾을 수 없거나 이미 삭제된 경우
     */
    void softDeleteUser(UUID userId);
}
