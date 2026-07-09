package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentResponse {
    private UUID id;
    private UUID loanId;
    private BigDecimal amount;
    private OffsetDateTime paidAt;
    private String method;
    private String recordedByName;
}
