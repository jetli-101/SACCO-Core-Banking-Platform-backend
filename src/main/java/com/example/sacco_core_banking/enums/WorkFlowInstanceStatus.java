package com.example.sacco_core_banking.enums;

/**
 * Lifecycle bucket of a WorkFlowInstance — drives the dashboard stat cards
 * (Active Processes, Rejected, ...) on the WorkFlow Instances screen.
 */
public enum WorkFlowInstanceStatus {
    INITIATED,
    IN_PROGRESS,
    COMPLETED,
    REJECTED
}
