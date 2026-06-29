package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    List<UserRole> findByUserId(UUID userId);

    List<UserRole> findByRoleId(UUID roleId);

    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);

    boolean existsByRoleId(UUID roleId);

    long countByRoleId(UUID roleId);

    void deleteByUserId(UUID userId);
}
