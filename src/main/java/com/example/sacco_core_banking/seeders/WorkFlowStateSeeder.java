package com.example.sacco_core_banking.seeders;

import com.example.sacco_core_banking.entities.WorkFlowState;
import com.example.sacco_core_banking.repositories.WorkFlowStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Bootstraps the fixed WorkFlowState catalogue (DRAFT/ACTIVE/ARCHIVED) on every startup — same pattern as RoleSeeder. */
@Component
@Order(1)
@RequiredArgsConstructor
public class WorkFlowStateSeeder implements CommandLineRunner {

    private final WorkFlowStateRepository workFlowStateRepository;

    @Override
    public void run(String... args) {
        seed("DRAFT", "Draft", "Stage is still being configured and is not yet usable by running processes");
        seed("ACTIVE", "Active", "Stage is live — processes may enter it");
        seed("ARCHIVED", "Archived", "Stage has been retired — kept for history but no longer enterable");
    }

    private void seed(String textId, String name, String description) {
        workFlowStateRepository.findByTextId(textId).orElseGet(() -> {
            WorkFlowState state = new WorkFlowState();
            state.setTextId(textId);
            state.setName(name);
            state.setDescription(description);
            return workFlowStateRepository.save(state);
        });
    }
}
