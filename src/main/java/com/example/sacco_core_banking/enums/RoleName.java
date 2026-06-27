package com.example.sacco_core_banking.enums;

/**
 * Fixed catalogue of roles the system understands. The Role entity stores the matching
 * `name()` string (e.g. "ROLE_MEMBER") so Spring Security's hasRole()/hasAuthority()
 * checks work directly against it. Seeded once on startup by RoleSeeder.
 */
public enum RoleName {
    ROLE_MEMBER,
    ROLE_TELLER,
    ROLE_LOAN_OFFICER,
    ROLE_ACCOUNTANT,
    ROLE_BRANCH_MANAGER,
    ROLE_SYSTEM_ADMINISTRATOR,
    ROLE_AUDITOR,
    ROLE_SUPER_ADMIN
}
