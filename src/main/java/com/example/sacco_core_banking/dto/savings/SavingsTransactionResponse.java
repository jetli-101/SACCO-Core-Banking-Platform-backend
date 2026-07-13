package com.example.sacco_core_banking.dto.savings;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.enums.SavingsTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsTransactionResponse {
    private UUID id;
    private UUID savingsAccountId;
    private SavingsTransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String method;
    private String narrative;
    private OffsetDateTime transactedAt;
    private String recordedByName;
}
