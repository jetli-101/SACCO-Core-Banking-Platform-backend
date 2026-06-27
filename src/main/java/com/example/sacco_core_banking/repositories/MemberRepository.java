package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUserId(UUID userId);

    List<Member> findBySaccoId(UUID saccoId);

    boolean existsBySaccoIdAndNationalId(UUID saccoId, String nationalId);

    @Query("select m from Member m where m.sacco.id = :saccoId and m.user.status = :status")
    List<Member> findBySaccoIdAndUserStatus(@Param("saccoId") UUID saccoId, @Param("status") UserStatus status);
}
