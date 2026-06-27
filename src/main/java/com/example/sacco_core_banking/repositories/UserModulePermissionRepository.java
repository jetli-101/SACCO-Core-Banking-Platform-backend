package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.UserModulePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModulePermissionRepository extends JpaRepository<UserModulePermission, UUID> {
    List<UserModulePermission> findByUserId(UUID userId);

    Optional<UserModulePermission> findByUserIdAndModuleRegisterId(UUID userId, UUID moduleId);
}
