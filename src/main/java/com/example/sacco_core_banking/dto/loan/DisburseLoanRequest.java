package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisburseLoanRequest {

    @NotNull(message = "Disbursed amount is required")
    @DecimalMin(value = "0.01", message = "Disbursed amount must be greater than zero")
    private BigDecimal disbursedAmount;
}
