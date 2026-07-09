package com.example.sacco_core_banking.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, UUID> {
    List<Repayment> findByLoanIdOrderByPaidAtDesc(UUID loanId);

    @Query("select coalesce(sum(r.amount), 0) from Repayment r where r.loan.id = :loanId")
    BigDecimal sumAmountByLoanId(@Param("loanId") UUID loanId);
}
