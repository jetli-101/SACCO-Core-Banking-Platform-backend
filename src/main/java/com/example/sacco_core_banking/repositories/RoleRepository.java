package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
