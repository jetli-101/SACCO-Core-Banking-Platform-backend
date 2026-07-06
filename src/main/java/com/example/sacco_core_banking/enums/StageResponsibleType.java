package com.example.sacco_core_banking.enums;

/** Which field on WorkFlowStage identifies who may act on it — only the matching field is consulted. */
public enum StageResponsibleType {
    GROUP,
    ROLE,
    USERS,
    ANY
}
