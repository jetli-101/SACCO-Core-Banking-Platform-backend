package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
}
