package com.example.sacco_core_banking.dto.loan;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.sacco_core_banking.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Loan's own business data plus the current status/stage/priority read straight off its linked WorkFlowInstance — the loan never stores those itself. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    private UUID id;
    private UUID memberId;
    private String memberName;
    private String memberNumber;
    private LoanType loanType;
    private BigDecimal amountApplied;
    private BigDecimal amountApproved;
    private BigDecimal interestRate;
    private Integer repaymentPeriodMonths;
    private String purpose;
    private BigDecimal disbursedAmount;
    private OffsetDateTime disbursedAt;
    private OffsetDateTime appliedAt;

    private UUID workflowInstanceId;
    private String status;
    private String currentStageName;
    private String priority;

    /** The loan's own lifecycle (PENDING/ACTIVE/DEFAULTED/CLOSED/REJECTED) — distinct from
     * `status` above, which only tracks the approval workflow. Computed at read time from
     * disbursement + repayments, never stored. */
    private String loanStatus;
    private BigDecimal totalRepaid;
    private BigDecimal outstandingBalance;
    private OffsetDateTime nextDueDate;
}
