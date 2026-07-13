package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.SavingsTransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A deposit or withdrawal recorded by staff (typically a Teller) against a savings account.
 * balanceAfter is snapshotted at write time so historical statements stay correct even if
 * later transactions change the account's current balance. */
@Entity
@Table(name = "smoothsurf_sacco_savings_transactions")
@Getter
@Setter
@NoArgsConstructor
public class SavingsTransaction extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccount savingsAccount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsTransactionType type;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "balance_after", nullable = false, precision = 15, scale = 2)
    private BigDecimal balanceAfter;

    private String method;

    @Column(columnDefinition = "TEXT")
    private String narrative;

    @Column(name = "transacted_at", nullable = false)
    private OffsetDateTime transactedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by_user_id")
    private User recordedBy;
}
