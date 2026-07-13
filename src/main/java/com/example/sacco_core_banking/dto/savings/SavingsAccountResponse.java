package com.example.sacco_core_banking.dto.savings;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.enums.SavingsAccountStatus;
import com.example.sacco_core_banking.enums.SavingsProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private String memberNumber;
    private UUID savingsProductId;
    private String savingsProductName;
    private SavingsProductType savingsProductType;
    private String accountNumber;
    private BigDecimal balance;
    private SavingsAccountStatus status;
    private OffsetDateTime openedAt;
    private OffsetDateTime closedAt;
}
