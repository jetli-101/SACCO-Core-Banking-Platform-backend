package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.example.sacco_core_banking.enums.GuarantorStatus;
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

/** A fellow member vouching for a portion of a loan application, pending their own response. */
@Entity
@Table(name = "smoothsurf_sacco_loan_guarantors")
@Getter
@Setter
@NoArgsConstructor
public class Guarantor extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "guaranteed_amount", precision = 15, scale = 2)
    private BigDecimal guaranteedAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GuarantorStatus status = GuarantorStatus.PENDING;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;
}
