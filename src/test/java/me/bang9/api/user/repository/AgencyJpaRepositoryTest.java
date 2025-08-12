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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
@DisplayName("AgencyJpaRepository 테스트 - PostGIS Container")
class AgencyJpaRepositoryTest {

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
    private AgencyJpaRepository agencyRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity testUser;
    private AgencyEntity testAgency;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 기관 생성
        testAgency = new AgencyEntity();
        testAgency.setName("테스트 기관");
        testAgency.setEmail("agency@example.com");
        testAgency.setAddress("서울시 강남구");
        testAgency.setContact("02-1234-5678");

        // Given: 테스트용 사용자 생성
        testUser = new UserEntity();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setNickname("testUser");
        testUser.setRole(UserRole.USER);
        testUser.setProvider(Provider.EMAIL);
    }

    @Test
    @DisplayName("기관명으로 기관 조회 - 존재하는 경우")
    void findByName_ShouldReturnAgency_WhenNameExists() {
        // Given
        agencyRepository.save(testAgency);
        
        // When
        Optional<AgencyEntity> result = agencyRepository.findByName("테스트 기관");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("테스트 기관");
        assertThat(result.get().getEmail()).isEqualTo("agency@example.com");
    }

    @Test
    @DisplayName("기관명으로 기관 조회 - 존재하지 않는 경우")
    void findByName_ShouldReturnEmpty_WhenNameNotExists() {
        // When
        Optional<AgencyEntity> result = agencyRepository.findByName("존재하지 않는 기관");
        
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("기관명 존재 여부 확인 - 존재하는 경우")
    void existsByName_ShouldReturnTrue_WhenNameExists() {
        // Given
        agencyRepository.save(testAgency);
        
        // When
        boolean exists = agencyRepository.existsByName("테스트 기관");
        
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("기관명 존재 여부 확인 - 존재하지 않는 경우")
    void existsByName_ShouldReturnFalse_WhenNameNotExists() {
        // When
        boolean exists = agencyRepository.existsByName("존재하지 않는 기관");
        
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("기관-사용자 OneToOne 관계 테스트 (기관이 대표자를 가짐)")
    void agencyUserRepresentation_ShouldWork() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        
        // Set both sides of bidirectional relationship
        testUser.setRepresentingAgency(savedAgency);
        savedAgency.setRepresentingUser(testUser);
        
        userRepository.save(testUser);
        agencyRepository.save(savedAgency);
        
        // When
        Optional<AgencyEntity> foundAgency = agencyRepository.findById(savedAgency.getId());
        
        // Then
        assertThat(foundAgency).isPresent();
        assertThat(foundAgency.get().getRepresentingUser()).isNotNull();
        assertThat(foundAgency.get().getRepresentingUser().getNickname()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("기관-사용자 ManyToMany 관계 테스트 (기관이 멤버를 가짐)")
    void agencyUserMembership_ShouldWork() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        testUser.getMemberAgencyList().add(savedAgency);
        savedAgency.getMembers().add(testUser);
        userRepository.save(testUser);
        
        // When
        Optional<AgencyEntity> foundAgency = agencyRepository.findById(savedAgency.getId());
        
        // Then
        assertThat(foundAgency).isPresent();
        assertThat(foundAgency.get().getMembers()).hasSize(1);
        assertThat(foundAgency.get().getMembers().iterator().next().getNickname()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("복합 관계 테스트 (대표자이면서 멤버인 경우)")
    void complexRelationship_RepresentativeAndMember_ShouldWork() {
        // Given
        AgencyEntity savedAgency = agencyRepository.save(testAgency);
        
        // Set both sides of bidirectional relationships
        testUser.setRepresentingAgency(savedAgency);
        savedAgency.setRepresentingUser(testUser);
        
        testUser.getMemberAgencyList().add(savedAgency);
        savedAgency.getMembers().add(testUser);
        
        userRepository.save(testUser);
        agencyRepository.save(savedAgency);
        
        // When
        Optional<AgencyEntity> foundAgency = agencyRepository.findById(savedAgency.getId());
        
        // Then
        assertThat(foundAgency).isPresent();
        AgencyEntity agency = foundAgency.get();
        
        // 대표자 확인
        assertThat(agency.getRepresentingUser()).isNotNull();
        assertThat(agency.getRepresentingUser().getNickname()).isEqualTo("testUser");
        
        // 멤버 확인
        assertThat(agency.getMembers()).hasSize(1);
        assertThat(agency.getMembers().iterator().next().getNickname()).isEqualTo("testUser");
        
        // 같은 사용자인지 확인
        assertThat(agency.getRepresentingUser().getId())
                .isEqualTo(agency.getMembers().iterator().next().getId());
    }
}