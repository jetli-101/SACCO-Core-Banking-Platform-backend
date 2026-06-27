package com.example.sacco_core_banking.seeders;

import com.example.sacco_core_banking.entities.Permission;
import com.example.sacco_core_banking.repositories.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Bootstraps the default grantable actions on every startup. Runs alongside RoleSeeder
 * (Order 1) since DataSeeder's permission grants need these rows to already exist.
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class PermissionSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;

    private static final String[][] DEFAULTS = {
            {"CREATE", "Create new records in a module"},
            {"READ", "View records in a module"},
            {"UPDATE", "Edit existing records in a module"},
            {"DELETE", "Remove records in a module"},
            {"APPROVE", "Approve or reject pending records in a module"},
    };

    @Override
    public void run(String... args) {
        for (String[] entry : DEFAULTS) {
            permissionRepository.findByName(entry[0])
                    .orElseGet(() -> permissionRepository.save(new Permission(entry[0], entry[1])));
        }
    }
}
