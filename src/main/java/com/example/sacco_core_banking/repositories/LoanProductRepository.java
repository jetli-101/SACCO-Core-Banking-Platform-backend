package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.LoanProduct;
import com.example.sacco_core_banking.enums.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
    Optional<LoanProduct> findByLoanType(LoanType loanType);
}
