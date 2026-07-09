package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;

import com.example.sacco_core_banking.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanProductRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0", message = "Interest rate cannot be negative")
    private BigDecimal interestRate;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.01", message = "Minimum amount must be greater than zero")
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.01", message = "Maximum amount must be greater than zero")
    private BigDecimal maxAmount;

    @NotNull(message = "Minimum term is required")
    @Min(value = 1, message = "Minimum term must be at least 1 month")
    private Integer minTermMonths;

    @NotNull(message = "Maximum term is required")
    @Min(value = 1, message = "Maximum term must be at least 1 month")
    private Integer maxTermMonths;

    private boolean active = true;
}
