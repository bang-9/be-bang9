package me.bang9.api.user.repository;

import me.bang9.api.user.entity.AgencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgencyJpaRepository extends JpaRepository<AgencyEntity, UUID> {

    /**
     * 기관명으로 기관 조회
     */
    Optional<AgencyEntity> findByName(String name);

    /**
     * 기관명 존재 여부 확인
     */
    boolean existsByName(String name);

    // TODO: Add complex relationship queries after basic methods are working
}
