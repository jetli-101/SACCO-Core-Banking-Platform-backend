package com.example.sacco_core_banking.entities;

import java.math.BigDecimal;

import com.example.sacco_core_banking.enums.LoanType;
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
 * Admin-configurable metadata (interest rate, limits, active toggle) for one LoanType.
 * Deliberately doesn't replace the LoanType enum on Loan — that column is used throughout
 * the application flow and workflow data snapshot already, so this sits alongside it as a
 * one-row-per-type configuration table instead of a risky schema migration.
 */
@Entity
@Table(name = "smoothsurf_sacco_loan_products")
@Getter
@Setter
@NoArgsConstructor
public class LoanProduct extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type", nullable = false, unique = true)
    private LoanType loanType;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull
    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @NotNull
    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @NotNull
    @Column(name = "min_term_months", nullable = false)
    private Integer minTermMonths;

    @NotNull
    @Column(name = "max_term_months", nullable = false)
    private Integer maxTermMonths;

    @Column(nullable = false)
    private boolean active = true;
}
