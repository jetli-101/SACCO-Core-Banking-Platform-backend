package com.example.sacco_core_banking.dto.savings;

import java.math.BigDecimal;

import com.example.sacco_core_banking.enums.SavingsProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SavingsProductRequest {

    @NotNull(message = "Savings product type is required")
    private SavingsProductType savingsProductType;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Interest rate is required")
    private BigDecimal interestRate;

    @NotNull(message = "Minimum opening balance is required")
    private BigDecimal minimumOpeningBalance;

    @NotNull(message = "Minimum balance is required")
    private BigDecimal minimumBalance;

    private boolean withdrawable = true;

    private boolean active = true;
}
