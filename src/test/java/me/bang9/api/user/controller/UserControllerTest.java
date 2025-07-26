package me.bang9.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.bang9.api.global.api.exception.Bang9Exception;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.model.Provider;
import me.bang9.api.user.service.UserAuthUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static me.bang9.api.global.api.code.status.UserErrorStatus.DUPLICATE_EMAIL;
import static me.bang9.api.global.api.code.status.UserErrorStatus.USER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(me.bang9.api.global.config.SecurityConfig.class)
@DisplayName("UserController 컨트롤러 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAuthUseCase userAuthUseCase;

    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        createRequest = new UserCreateRequest(
                "test@example.com",
                "password123!",
                "testuser",
                Provider.EMAIL
        );

        updateRequest = new UserUpdateRequest("updatedNickname");

        userResponse = new UserResponse(
                testUserId,
                "test@example.com",
                "testuser",
                "USER",
                "EMAIL",
                new HashSet<>()
        );
    }

    @Nested
    @DisplayName("POST /v1/users - 사용자 생성")
    class CreateUserTest {

        @Test
        @DisplayName("새 사용자 생성 성공")
        @WithMockUser
        void createUser_Success() throws Exception {
            // Given
            given(userAuthUseCase.createUser(any(UserCreateRequest.class)))
                    .willReturn(userResponse);

            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON-201"))
                    .andExpect(jsonPath("$.message").value("Resource was created successfully"))
                    .andExpect(jsonPath("$.result.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.result.email").value("test@example.com"))
                    .andExpect(jsonPath("$.result.nickname").value("testuser"))
                    .andExpect(jsonPath("$.result.role").value("USER"))
                    .andExpect(jsonPath("$.result.provider").value("EMAIL"));
        }

        @Test
        @DisplayName("중복 이메일로 사용자 생성 실패")
        @WithMockUser
        void createUser_ShouldFail_WhenEmailAlreadyExists() throws Exception {
            // Given
            given(userAuthUseCase.createUser(any(UserCreateRequest.class)))
                    .willThrow(new Bang9Exception(DUPLICATE_EMAIL));

            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest))
                            .with(csrf()))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value(DUPLICATE_EMAIL.getCode()))
                    .andExpect(jsonPath("$.message").value(DUPLICATE_EMAIL.getMessage()));
        }

        @Test
        @DisplayName("잘못된 요청 데이터로 사용자 생성 실패")
        @WithMockUser
        void createUser_ShouldFail_WhenInvalidRequest() throws Exception {
            // Given - invalid email format
            UserCreateRequest invalidRequest = new UserCreateRequest(
                    "invalid-email",
                    "password123!",
                    "testuser",
                    Provider.EMAIL
            );

            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("요청 본문 없이 사용자 생성 실패")
        @WithMockUser
        void createUser_ShouldFail_WhenNoRequestBody() throws Exception {
            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /v1/users - 모든 사용자 조회")
    class GetAllUsersTest {

        @Test
        @DisplayName("모든 사용자 조회 성공")
        @WithMockUser
        void getAllUsers_Success() throws Exception {
            // Given
            UserResponse user2 = new UserResponse(
                    UUID.randomUUID(),
                    "user2@example.com",
                    "user2",
                    "USER",
                    "EMAIL",
                    new HashSet<>()
            );
            
            given(userAuthUseCase.getAllUsers()).willReturn(Arrays.asList(userResponse, user2));

            // When & Then
            mockMvc.perform(get("/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON-200"))
                    .andExpect(jsonPath("$.message").value("Request was successful"))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.length()").value(2))
                    .andExpect(jsonPath("$.result[0].email").value("test@example.com"))
                    .andExpect(jsonPath("$.result[1].email").value("user2@example.com"));
        }

        @Test
        @DisplayName("사용자가 없는 경우 빈 배열 반환")
        @WithMockUser
        void getAllUsers_ShouldReturnEmptyArray_WhenNoUsers() throws Exception {
            // Given
            given(userAuthUseCase.getAllUsers()).willReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/v1/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /v1/users/{userId} - 사용자 ID로 조회")
    class GetUserByIdTest {

        @Test
        @DisplayName("사용자 ID로 조회 성공")
        @WithMockUser
        void getUserById_Success() throws Exception {
            // Given
            given(userAuthUseCase.getUserById(testUserId)).willReturn(userResponse);

            // When & Then
            mockMvc.perform(get("/v1/users/{userId}", testUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON-200"))
                    .andExpect(jsonPath("$.result.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.result.email").value("test@example.com"))
                    .andExpect(jsonPath("$.result.nickname").value("testuser"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회 실패")
        @WithMockUser
        void getUserById_ShouldFail_WhenUserNotFound() throws Exception {
            // Given
            given(userAuthUseCase.getUserById(testUserId))
                    .willThrow(new Bang9Exception(USER_NOT_FOUND));

            // When & Then
            mockMvc.perform(get("/v1/users/{userId}", testUserId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.getCode()))
                    .andExpect(jsonPath("$.message").value(USER_NOT_FOUND.getMessage()));
        }

        @Test
        @DisplayName("잘못된 UUID 형식으로 조회 실패")
        @WithMockUser
        void getUserById_ShouldFail_WhenInvalidUUID() throws Exception {
            // When & Then
            mockMvc.perform(get("/v1/users/{userId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /v1/users/{userId} - 사용자 정보 수정")
    class UpdateUserTest {

        @Test
        @DisplayName("사용자 정보 수정 성공")
        @WithMockUser
        void updateUser_Success() throws Exception {
            // Given
            UserResponse updatedResponse = new UserResponse(
                    testUserId,
                    "test@example.com",
                    "updatedNickname",
                    "USER",
                    "EMAIL",
                    new HashSet<>()
            );
            
            given(userAuthUseCase.updateUser(eq(testUserId), any(UserUpdateRequest.class)))
                    .willReturn(updatedResponse);

            // When & Then
            mockMvc.perform(patch("/v1/users/{userId}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON-200"))
                    .andExpect(jsonPath("$.result.id").value(testUserId.toString()))
                    .andExpect(jsonPath("$.result.nickname").value("updatedNickname"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 실패")
        @WithMockUser
        void updateUser_ShouldFail_WhenUserNotFound() throws Exception {
            // Given
            given(userAuthUseCase.updateUser(eq(testUserId), any(UserUpdateRequest.class)))
                    .willThrow(new Bang9Exception(USER_NOT_FOUND));

            // When & Then
            mockMvc.perform(patch("/v1/users/{userId}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest))
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("잘못된 요청 데이터로 사용자 수정 실패")
        @WithMockUser
        void updateUser_ShouldFail_WhenInvalidRequest() throws Exception {
            // Given - invalid nickname (empty)
            UserUpdateRequest invalidRequest = new UserUpdateRequest("");

            // When & Then
            mockMvc.perform(patch("/v1/users/{userId}", testUserId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/users/{userId} - 사용자 소프트 삭제")
    class SoftDeleteUserTest {

        @Test
        @DisplayName("사용자 소프트 삭제 성공")
        @WithMockUser
        void softDeleteUser_Success() throws Exception {
            // When & Then
            mockMvc.perform(delete("/v1/users/{userId}", testUserId)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.code").value("COMMON-200"))
                    .andExpect(jsonPath("$.message").value("Request was successful"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 실패")
        @WithMockUser
        void softDeleteUser_ShouldFail_WhenUserNotFound() throws Exception {
            // Given
            willThrow(new Bang9Exception(USER_NOT_FOUND))
                    .given(userAuthUseCase).softDeleteUser(testUserId);

            // When & Then
            mockMvc.perform(delete("/v1/users/{userId}", testUserId)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.isSuccess").value(false))
                    .andExpect(jsonPath("$.code").value(USER_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("잘못된 UUID 형식으로 삭제 실패")
        @WithMockUser
        void softDeleteUser_ShouldFail_WhenInvalidUUID() throws Exception {
            // When & Then
            mockMvc.perform(delete("/v1/users/{userId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }
}