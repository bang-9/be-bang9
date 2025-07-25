package me.bang9.api.user.repository;

import me.bang9.api.user.entity.AgencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgencyJpaRepository extends JpaRepository<AgencyEntity, UUID> {
}
