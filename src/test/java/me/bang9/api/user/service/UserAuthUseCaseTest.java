package me.bang9.api.user.service;

import me.bang9.api.global.api.exception.Bang9Exception;
import me.bang9.api.user.dto.req.UserCreateRequest;
import me.bang9.api.user.dto.req.UserUpdateRequest;
import me.bang9.api.user.dto.res.UserResponse;
import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.model.Provider;
import me.bang9.api.user.model.UserRole;
import me.bang9.api.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static me.bang9.api.global.api.code.status.UserErrorStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthUseCase 서비스 테스트")
class UserAuthUseCaseTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserAuthUseCase userAuthUseCase;

    private UserEntity testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    // Helper method to create a deleted user for testing
    private UserEntity createDeletedUser() {
        UserEntity deletedUser = new UserEntity();
        deletedUser.setId(UUID.randomUUID());
        deletedUser.setEmail("deleted@example.com");
        deletedUser.setPassword("password");
        deletedUser.setNickname("deleteduser");
        deletedUser.setRole(UserRole.USER);
        deletedUser.setProvider(Provider.EMAIL);
        return deletedUser;
    }
    
    // Helper method to simulate deleted state using reflection
    private void markAsDeleted(UserEntity entity) {
        try {
            var statusField = entity.getClass().getSuperclass().getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(entity, Boolean.FALSE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark entity as deleted", e);
        }
    }

    @BeforeEach
    void setUp() {
        // Inject the actual service implementation with mocked dependencies
        userAuthUseCase = new UserAuthService(userRepository, passwordEncoder);

        // Test data setup
        testUser = new UserEntity();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setNickname("testuser");
        testUser.setRole(UserRole.USER);
        testUser.setProvider(Provider.EMAIL);

        createRequest = new UserCreateRequest(
                "new@example.com",
                "password123!",
                "newuser",
                Provider.EMAIL
        );

        updateRequest = new UserUpdateRequest("updatedNickname");
    }

    @Nested
    @DisplayName("사용자 생성 테스트")
    class CreateUserTest {

        @Test
        @DisplayName("새 사용자 생성 성공")
        void createUser_Success() {
            // Given
            given(userRepository.existsByEmail(createRequest.email())).willReturn(false);
            given(passwordEncoder.encode(createRequest.password())).willReturn("encodedPassword");
            given(userRepository.save(any(UserEntity.class))).willReturn(testUser);

            // When
            UserResponse result = userAuthUseCase.createUser(createRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(testUser.getEmail());
            assertThat(result.nickname()).isEqualTo(testUser.getNickname());
        }

        @Test
        @DisplayName("이메일 중복으로 인한 사용자 생성 실패")
        void createUser_ShouldFail_WhenEmailAlreadyExists() {
            // Given
            given(userRepository.existsByEmail(createRequest.email())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.createUser(createRequest))
                    .isInstanceOf(Bang9Exception.class);
        }
    }

    @Nested
    @DisplayName("모든 사용자 조회 테스트")
    class GetAllUsersTest {

        @Test
        @DisplayName("모든 사용자 조회 성공")
        void getAllUsers_Success() {
            // Given
            UserEntity user2 = new UserEntity();
            user2.setId(UUID.randomUUID());
            user2.setEmail("user2@example.com");
            user2.setPassword("password");
            user2.setNickname("user2");
            user2.setRole(UserRole.USER);
            user2.setProvider(Provider.EMAIL);

            given(userRepository.findAll()).willReturn(Arrays.asList(testUser, user2));

            // When
            var result = userAuthUseCase.getAllUsers();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).email()).isEqualTo(testUser.getEmail());
            assertThat(result.get(1).email()).isEqualTo(user2.getEmail());
        }

        @Test
        @DisplayName("사용자가 없는 경우 빈 리스트 반환")
        void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
            // Given
            given(userRepository.findAll()).willReturn(Collections.emptyList());

            // When
            var result = userAuthUseCase.getAllUsers();

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("사용자 ID로 조회 테스트")
    class GetUserByIdTest {

        @Test
        @DisplayName("사용자 ID로 조회 성공")
        void getUserById_Success() {
            // Given
            UUID userId = testUser.getId();
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));

            // When
            UserResponse result = userAuthUseCase.getUserById(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.email()).isEqualTo(testUser.getEmail());
            assertThat(result.nickname()).isEqualTo(testUser.getNickname());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회 실패")
        void getUserById_ShouldFail_WhenUserNotFound() {
            // Given
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.getUserById(userId))
                    .isInstanceOf(Bang9Exception.class);
        }

        @Test
        @DisplayName("삭제된 사용자 조회 실패")
        void getUserById_ShouldFail_WhenUserIsDeleted() {
            // Given
            UUID userId = testUser.getId();
            UserEntity deletedUser = createDeletedUser();
            markAsDeleted(deletedUser); // Actually mark as deleted
            given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.getUserById(userId))
                    .isInstanceOf(Bang9Exception.class);
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("사용자 닉네임 수정 성공")
        void updateUser_Success() {
            // Given
            UUID userId = testUser.getId();
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));
            given(userRepository.save(any(UserEntity.class))).willReturn(testUser);

            // When
            UserResponse result = userAuthUseCase.updateUser(userId, updateRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.nickname()).isEqualTo(updateRequest.nickname());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 실패")
        void updateUser_ShouldFail_WhenUserNotFound() {
            // Given
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.updateUser(userId, updateRequest))
                    .isInstanceOf(Bang9Exception.class);
        }

        @Test
        @DisplayName("삭제된 사용자 수정 실패")
        void updateUser_ShouldFail_WhenUserIsDeleted() {
            // Given
            UUID userId = testUser.getId();
            UserEntity deletedUser = createDeletedUser();
            markAsDeleted(deletedUser); // Actually mark as deleted
            given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.updateUser(userId, updateRequest))
                    .isInstanceOf(Bang9Exception.class);
        }
    }

    @Nested
    @DisplayName("사용자 소프트 삭제 테스트")
    class SoftDeleteUserTest {

        @Test
        @DisplayName("사용자 소프트 삭제 성공")
        void softDeleteUser_Success() {
            // Given
            UUID userId = testUser.getId();
            given(userRepository.findById(userId)).willReturn(Optional.of(testUser));

            // When & Then
            assertThatCode(() -> userAuthUseCase.softDeleteUser(userId))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 실패")
        void softDeleteUser_ShouldFail_WhenUserNotFound() {
            // Given
            UUID userId = UUID.randomUUID();
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.softDeleteUser(userId))
                    .isInstanceOf(Bang9Exception.class);
        }

        @Test
        @DisplayName("이미 삭제된 사용자 삭제 실패")
        void softDeleteUser_ShouldFail_WhenUserAlreadyDeleted() {
            // Given
            UUID userId = testUser.getId();
            UserEntity deletedUser = createDeletedUser();
            markAsDeleted(deletedUser); // Actually mark as deleted first
            given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

            // When & Then
            assertThatThrownBy(() -> userAuthUseCase.softDeleteUser(userId))
                    .isInstanceOf(Bang9Exception.class);
        }
    }
}