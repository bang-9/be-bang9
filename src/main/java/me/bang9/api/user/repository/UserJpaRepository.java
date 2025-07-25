package me.bang9.api.user.repository;

import me.bang9.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * 이메일로 사용자 조회
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * 닉네임으로 사용자 조회
     */
    Optional<UserEntity> findByNickname(String nickname);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 특정 기관을 대표하는 사용자들 조회
     */
    @Query("SELECT u FROM UserEntity u WHERE u.representingAgency.id = :agencyId")
    List<UserEntity> findUsersRepresentingAgency(@Param("agencyId") UUID agencyId);
    
    /**
     * 특정 사용자가 멤버로 속한 기관들의 다른 멤버들 조회
     */
    @Query("SELECT DISTINCT u FROM UserEntity u JOIN u.memberAgencyList a WHERE a IN (SELECT ma FROM UserEntity user JOIN user.memberAgencyList ma WHERE user.id = :userId) AND u.id != :userId")
    List<UserEntity> findCoMembersInSameAgencies(@Param("userId") UUID userId);
}
