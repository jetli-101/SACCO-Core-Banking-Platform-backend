package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRoleId(UUID roleId);
}
