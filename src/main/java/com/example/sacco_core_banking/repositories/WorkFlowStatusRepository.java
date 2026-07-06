package com.example.sacco_core_banking.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStatusRepository extends JpaRepository<WorkFlowStatus, UUID> {
    Optional<WorkFlowStatus> findByTextId(String textId);

    Optional<WorkFlowStatus> findByName(String name);
}
