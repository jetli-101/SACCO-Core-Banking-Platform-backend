package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;

import com.example.sacco_core_banking.enums.SavingsProductType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin-configurable metadata (interest rate, minimum balances, withdrawability) for one
 * SavingsProductType — same one-row-per-type shape as LoanProduct. SHARE_CAPITAL accounts
 * are modelled as a savings product rather than a separate entity: in a Kenyan Sacco a
 * member's share capital is functionally a non-withdrawable savings balance, so reusing this
 * table (with withdrawable=false) avoids duplicating account/transaction plumbing for it.
 */
@Entity
@Table(name = "smoothsurf_sacco_savings_products")
@Getter
@Setter
@NoArgsConstructor
public class SavingsProduct extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "savings_product_type", nullable = false, unique = true)
    private SavingsProductType savingsProductType;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull
    @Column(name = "minimum_opening_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumOpeningBalance;

    @NotNull
    @Column(name = "minimum_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumBalance;

    /** Ordinary savings can be withdrawn on demand; share capital and fixed deposits typically can't. */
    @Column(nullable = false)
    private boolean withdrawable = true;

    @Column(nullable = false)
    private boolean active = true;
}
