package com.example.sacco_core_banking.classes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reducing-balance loan math — mirrors the frontend's utils/loanCalculations.ts exactly,
 * so the EMI/schedule a member previews while applying matches what the backend later uses
 * to judge whether a disbursed loan is on track, overdue, or fully repaid.
 */
public final class LoanAmortizationCalculator {

    private LoanAmortizationCalculator() {
    }

    public static BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        if (months <= 0) return BigDecimal.ZERO;
        if (annualRatePercent == null || annualRatePercent.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(months), MathContext.DECIMAL64);
        }
        BigDecimal monthlyRate = annualRatePercent.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64);
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months, MathContext.DECIMAL64);
        return principal.multiply(monthlyRate).multiply(factor).divide(factor.subtract(BigDecimal.ONE), MathContext.DECIMAL64);
    }

    public record ScheduleRow(int installmentNo, OffsetDateTime dueDate, BigDecimal principal, BigDecimal interest, BigDecimal installment, BigDecimal closingBalance) {
    }

    public static List<ScheduleRow> buildSchedule(BigDecimal principal, BigDecimal annualRatePercent, int months, OffsetDateTime startDate) {
        List<ScheduleRow> rows = new ArrayList<>();
        if (principal == null || principal.signum() <= 0 || months <= 0) return rows;

        BigDecimal emi = calculateEmi(principal, annualRatePercent, months);
        BigDecimal monthlyRate = annualRatePercent == null ? BigDecimal.ZERO : annualRatePercent.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64);

        BigDecimal balance = principal;
        for (int i = 1; i <= months; i++) {
            BigDecimal interest = balance.multiply(monthlyRate, MathContext.DECIMAL64);
            BigDecimal principalComponent = emi.subtract(interest).min(balance);
            BigDecimal closingBalance = balance.subtract(principalComponent).max(BigDecimal.ZERO);
            rows.add(new ScheduleRow(i, startDate.plusMonths(i), principalComponent.setScale(2, RoundingMode.HALF_UP), interest.setScale(2, RoundingMode.HALF_UP),
                    principalComponent.add(interest).setScale(2, RoundingMode.HALF_UP), closingBalance.setScale(2, RoundingMode.HALF_UP)));
            balance = closingBalance;
        }
        return rows;
    }

    public static BigDecimal totalDue(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        return calculateEmi(principal, annualRatePercent, months).multiply(BigDecimal.valueOf(months));
    }
}
