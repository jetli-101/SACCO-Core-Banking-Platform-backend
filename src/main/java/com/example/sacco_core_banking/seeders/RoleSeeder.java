package com.example.sacco_core_banking.seeders;

import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.enums.RoleName;
import com.example.sacco_core_banking.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Bootstraps the fixed role catalogue on every startup. Runs first (Order 1) because
 * DataSeeder needs roles to already exist before it can assign one to a seeded user.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName.name())
                    .orElseGet(() -> roleRepository.save(new Role(roleName.name(), description(roleName))));
        }
    }

    private String description(RoleName roleName) {
        return switch (roleName) {
            case ROLE_MEMBER -> "SACCO member with access to their own accounts, loans, and contributions";
            case ROLE_TELLER -> "Front-office staff handling deposits, withdrawals, and cash transactions";
            case ROLE_LOAN_OFFICER -> "Staff who appraise, approve, and disburse member loans";
            case ROLE_ACCOUNTANT -> "Staff responsible for ledgers, reconciliations, and financial reporting";
            case ROLE_BRANCH_MANAGER -> "Branch-level oversight of staff, approvals, and operations";
            case ROLE_SYSTEM_ADMINISTRATOR -> "Manages users, roles, and system configuration for a Sacco";
            case ROLE_AUDITOR -> "Read-only access for compliance review and audit trails";
            case ROLE_SUPER_ADMIN -> "Platform-level administrator with cross-Sacco access";
        };
    }
}
