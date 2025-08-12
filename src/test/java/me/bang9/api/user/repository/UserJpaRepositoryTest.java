package me.bang9.api.user.repository;

import me.bang9.api.user.entity.AgencyEntity;
import me.bang9.api.user.entity.UserEntity;
import me.bang9.api.user.model.Provider;
import me.bang9.api.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("UserJpaRepository 테스트 - PostGIS Container")
class UserJpaRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgis = new PostgreSQLContainer<>(
            DockerImageName.parse("imresamu/postgis-arm64:17-3.5")
                    .asCompatibleSubstituteFor("postgres")
    )
    .withDatabaseName("bang9")
    .withUsername("bang9")
    .withPassword("testpass")
    .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF8 --lc-collate=C.UTF-8 --lc-ctype=C.UTF-8")
    .withInitScript("init-test-postgis.sql")
    .withReuse(false)
    .withStartupTimeoutSeconds(60)
    .withConnectTimeoutSeconds(20);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgis::getJdbcUrl);
        registry.add("spring.datasource.username", postgis::getUsername);
        registry.add("spring.datasource.password", postgis::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
        
        // Optimized for fast shutdown
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "2");
        registry.add("spring.datasource.hikari.minimum-idle", () -> "0");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "3000");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "10000");
        registry.add("spring.datasource.hikari.idle-timeout", () -> "3000");
    }

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private AgencyJpaRepository agencyRepository;

    private UserEntity testUser;
    private AgencyEntity testAgency;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 사용자 생성
        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setNickname("testUser");
        testUser.setRole(UserRole.USER);
        testUser.setProvider(Provider.EMAIL);

        // Given: 테스트용 기관 생성
        testAgency = new AgencyEntity();
        testAgency.setName("테스트 기관");
        testAgency.setEmail("agency@example.com");
        testAgency.setAddress("서울시 강남구");
        testAgency.setContact("02-1234-5678");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 존재하는 경우")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<UserEntity> result = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getNickname()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 - 존재하지 않는 경우")
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // When
        Optional<UserEntity> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 - 존재하는 경우")
    void findByNickname_ShouldReturnUser_WhenNicknameExists() {
        // Given
        userRepository.save(testUser);

        // When
        Optional<UserEntity> result = userRepository.findByNickname("testUser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getNickname()).isEqualTo("testUser");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 - 존재하지 않는 경우")
    void findByNickname_ShouldReturnEmpty_WhenNicknameNotExists() {
        // When
        Optional<UserEntity> result = userRepository.findByNickname("nonexistentUser");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        userRepository.save(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자-기관 OneToOne 관계 테스트")
    void userAgencyRepresentation_ShouldWork() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        testUser.setRepresentingAgency(savedAgency);
        UserEntity savedUser = userRepository.save(testUser);

        // When
        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRepresentingAgency()).isNotNull();
        assertThat(foundUser.get().getRepresentingAgency().getName()).isEqualTo("테스트 기관");
    }

    @Test
    @DisplayName("사용자-기관 ManyToMany 관계 테스트")
    void userAgencyMembership_ShouldWork() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        testUser.getMemberAgencyList().add(savedAgency);
        savedAgency.getMembers().add(testUser);
        UserEntity savedUser = userRepository.save(testUser);

        // When
        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getMemberAgencyList()).hasSize(1);
        assertThat(foundUser.get().getMemberAgencyList().iterator().next().getName()).isEqualTo("테스트 기관");
    }

    @Test
    @DisplayName("특정 기관을 대표하는 사용자들 조회 - TDD 실패 테스트")
    void findUsersRepresentingAgency_ShouldReturnRepresentatives() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        
        // 첫 번째 사용자를 대표자로 설정
        testUser.setRepresentingAgency(savedAgency);
        savedAgency.setRepresentingUser(testUser);
        userRepository.save(testUser);
        agencyRepository.save(savedAgency);

        // When
        List<UserEntity> representatives = userRepository.findUsersRepresentingAgency(savedAgency.getId());

        // Then
        assertThat(representatives).hasSize(1);
        assertThat(representatives.get(0).getNickname()).isEqualTo("testUser");
        assertThat(representatives.get(0).getRepresentingAgency().getName()).isEqualTo("테스트 기관");
    }

    @Test
    @DisplayName("같은 기관의 다른 멤버들 조회 - TDD 실패 테스트")
    void findCoMembersInSameAgencies_ShouldReturnCoMembers() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        
        // 첫 번째 사용자
        testUser.getMemberAgencyList().add(savedAgency);
        savedAgency.getMembers().add(testUser);
        UserEntity savedUser1 = userRepository.save(testUser);
        
        // 두 번째 사용자 (같은 기관 멤버)
        UserEntity testUser2 = new UserEntity();
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("password123");
        testUser2.setNickname("testUser2");
        testUser2.setRole(UserRole.USER);
        testUser2.setProvider(Provider.EMAIL);
        testUser2.getMemberAgencyList().add(savedAgency);
        savedAgency.getMembers().add(testUser2);
        userRepository.save(testUser2);
        
        agencyRepository.save(savedAgency);

        // When
        List<UserEntity> coMembers = userRepository.findCoMembersInSameAgencies(savedUser1.getId());

        // Then
        assertThat(coMembers).hasSize(1);
        assertThat(coMembers.get(0).getNickname()).isEqualTo("testUser2");
    }
}