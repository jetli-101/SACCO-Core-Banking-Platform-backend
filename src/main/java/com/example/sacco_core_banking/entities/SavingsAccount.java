package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.SavingsAccountStatus;
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

/**
 * A member's savings account against one SavingsProduct. Unlike Loan, opening an account
 * doesn't go through the workflow engine — deposit-taking accounts don't carry the same
 * credit-risk approval need a loan does, so this activates immediately at ACTIVE status.
 * balance is maintained here (not derived from transactions on read) so account listings
 * stay a single-table query; SavingsTransactionService is the only writer of both.
 */
@Entity
@Table(name = "smoothsurf_sacco_savings_accounts")
@Getter
@Setter
@NoArgsConstructor
public class SavingsAccount extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sacco_id", nullable = false)
    private Sacco sacco;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "savings_product_id", nullable = false)
    private SavingsProduct savingsProduct;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @NotNull
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsAccountStatus status = SavingsAccountStatus.ACTIVE;

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;
}
