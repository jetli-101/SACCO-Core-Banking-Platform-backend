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
public class GuarantorResponse {
    private UUID id;
    private UUID loanId;
    private UUID memberId;
    private String memberName;
    private String memberNumber;
    private String memberAvatarUrl;
    private BigDecimal guaranteedAmount;
    private String status;
    private OffsetDateTime respondedAt;
}
