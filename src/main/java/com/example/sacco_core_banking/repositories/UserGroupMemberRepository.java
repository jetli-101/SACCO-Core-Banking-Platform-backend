package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.UserGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupMemberRepository extends JpaRepository<UserGroupMember, UUID> {
    List<UserGroupMember> findByUserId(UUID userId);

    List<UserGroupMember> findByUserGroupId(UUID userGroupId);

    Optional<UserGroupMember> findByUserIdAndUserGroupId(UUID userId, UUID userGroupId);

    long countByUserGroupId(UUID userGroupId);

    boolean existsByUserGroupId(UUID userGroupId);

    void deleteByUserId(UUID userId);
}
