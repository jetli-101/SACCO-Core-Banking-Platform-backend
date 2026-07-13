package com.example.sacco_core_banking.dto.savings;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.sacco_core_banking.enums.SavingsProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProductResponse {
    private UUID id;
    private SavingsProductType savingsProductType;
    private String name;
    private String description;
    private BigDecimal interestRate;
    private BigDecimal minimumOpeningBalance;
    private BigDecimal minimumBalance;
    private boolean withdrawable;
    private boolean active;
}
