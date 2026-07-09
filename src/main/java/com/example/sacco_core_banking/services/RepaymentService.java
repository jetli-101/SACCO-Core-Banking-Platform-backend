package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.loan.RepaymentRequest;
import com.example.sacco_core_banking.dto.loan.RepaymentResponse;
import com.example.sacco_core_banking.entities.Loan;
import com.example.sacco_core_banking.entities.Repayment;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.repositories.LoanRepository;
import com.example.sacco_core_banking.repositories.RepaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Repayment transactions recorded by staff against a disbursed loan. */
@Service
@Transactional
public class RepaymentService {

    @Autowired
    private RepaymentRepository repaymentRepository;
    @Autowired
    private LoanRepository loanRepository;

    public List<RepaymentResponse> listRepayments(UUID loanId) {
        return repaymentRepository.findByLoanIdOrderByPaidAtDesc(loanId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RepaymentResponse recordRepayment(UUID loanId, RepaymentRequest request, User recordedBy) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getDisbursedAt() == null) {
            throw new IllegalStateException("Cannot record a repayment against a loan that hasn't been disbursed");
        }

        Repayment repayment = new Repayment();
        repayment.setLoan(loan);
        repayment.setAmount(request.getAmount());
        repayment.setPaidAt(request.getPaidAt() != null ? request.getPaidAt() : OffsetDateTime.now());
        repayment.setMethod(request.getMethod());
        repayment.setRecordedBy(recordedBy);

        return toResponse(repaymentRepository.save(repayment));
    }

    private RepaymentResponse toResponse(Repayment repayment) {
        return RepaymentResponse.builder()
                .id(repayment.getId())
                .loanId(repayment.getLoan().getId())
                .amount(repayment.getAmount())
                .paidAt(repayment.getPaidAt())
                .method(repayment.getMethod())
                .recordedByName(repayment.getRecordedBy() != null ? repayment.getRecordedBy().getUsername() : null)
                .build();
    }
}
