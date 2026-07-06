package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStateRepository extends JpaRepository<WorkFlowState, UUID> {
    Optional<WorkFlowState> findByTextId(String textId);

    Optional<WorkFlowState> findByName(String name);
}
