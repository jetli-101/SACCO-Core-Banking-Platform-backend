package com.example.sacco_core_banking.dto.savings;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OpenSavingsAccountRequest {

    @NotNull(message = "Savings product is required")
    private UUID savingsProductId;

    /** Optional initial deposit recorded as the account's first transaction, on top of the
     * product's minimumOpeningBalance requirement. */
    private BigDecimal openingDeposit;
}
