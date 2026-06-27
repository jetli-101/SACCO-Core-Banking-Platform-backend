package com.example.sacco_core_banking.enums;

/**
 * Lifecycle status of a User account. Never delete a user — transition status instead
 * so the audit trail (AuditLog / MemberApproval) stays meaningful.
 */
public enum UserStatus {
    PENDING,
    ACTIVE,
    INACTIVE,
    REJECTED,
    SUSPENDED,
    LOCKED,
    DEACTIVATED
}
