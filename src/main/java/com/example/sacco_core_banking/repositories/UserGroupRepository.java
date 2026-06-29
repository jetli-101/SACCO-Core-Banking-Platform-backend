package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {
    Optional<UserGroup> findByName(String name);
}
