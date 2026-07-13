package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
    List<SavingsAccount> findBySaccoId(UUID saccoId);

    List<SavingsAccount> findByMemberId(UUID memberId);

    Optional<SavingsAccount> findByAccountNumber(String accountNumber);

    boolean existsByMemberIdAndSavingsProductId(UUID memberId, UUID savingsProductId);
}
