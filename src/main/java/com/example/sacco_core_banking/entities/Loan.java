package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.enums.LoanType;
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
 * A member's loan application. Deliberately carries only the loan's own business data —
 * its lifecycle status and current stage live on the linked WorkFlowInstance (via
 * workflowInstanceId), same separation the engine already keeps everywhere else, so this
 * entity never has to duplicate or resync a status the workflow already tracks.
 */
@Entity
@Table(name = "smoothsurf_sacco_loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sacco_id", nullable = false)
    private Sacco sacco;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @NotNull
    @Column(name = "amount_applied", nullable = false, precision = 15, scale = 2)
    private BigDecimal amountApplied;

    @Column(name = "amount_approved", precision = 15, scale = 2)
    private BigDecimal amountApproved;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull
    @Column(name = "repayment_period_months", nullable = false)
    private Integer repaymentPeriodMonths;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    /** Set once startProcess() attaches this application to the engine — null only in the instant between save and that call. */
    @Column(name = "workflow_instance_id")
    private UUID workflowInstanceId;

    @Column(name = "disbursed_amount", precision = 15, scale = 2)
    private BigDecimal disbursedAmount;

    @Column(name = "disbursed_at")
    private OffsetDateTime disbursedAt;

    @Column(name = "applied_at", nullable = false)
    private OffsetDateTime appliedAt;
}
