package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.sacco_core_banking.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductResponse {
    private UUID id;
    private LoanType loanType;
    private String name;
    private String description;
    private BigDecimal interestRate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minTermMonths;
    private Integer maxTermMonths;
    private boolean active;
}
