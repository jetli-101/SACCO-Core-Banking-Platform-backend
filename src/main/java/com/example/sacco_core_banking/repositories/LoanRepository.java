package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findBySaccoId(UUID saccoId);

    List<Loan> findByMemberId(UUID memberId);
}
