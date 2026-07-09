package com.example.sacco_core_banking.enums;

/**
 * The loan's own lifecycle — distinct from the workflow instance's approval status
 * (INITIATED/IN_PROGRESS/COMPLETED/REJECTED), which only tracks the approval process.
 * This tracks what happens to the money after approval: PENDING until disbursed, ACTIVE
 * while being repaid, DEFAULTED once repayments fall more than 30 days behind schedule,
 * CLOSED once fully repaid, or REJECTED if the approval workflow rejected it.
 */
public enum LoanStatus {
    PENDING,
    ACTIVE,
    DEFAULTED,
    CLOSED,
    REJECTED
}
