package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Adds a fellow member as a guarantor on the caller's own loan application. */
@Data
public class GuarantorRequest {

    @NotNull(message = "Member is required")
    private UUID memberId;

    private BigDecimal guaranteedAmount;
}
