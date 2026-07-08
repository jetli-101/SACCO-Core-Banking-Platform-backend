package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;

import com.example.sacco_core_banking.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** What the logged-in member submits to apply for a loan — memberId is resolved server-side from the caller, never taken from the request. */
@Data
public class LoanRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Amount applied is required")
    @DecimalMin(value = "0.01", message = "Amount applied must be greater than zero")
    private BigDecimal amountApplied;

    @NotNull(message = "Repayment period is required")
    @Min(value = 1, message = "Repayment period must be at least 1 month")
    private Integer repaymentPeriodMonths;

    private String purpose;
}
