package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowInstanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowInstanceHistoryRepository extends JpaRepository<WorkFlowInstanceHistory, UUID> {
    List<WorkFlowInstanceHistory> findByInstanceIdOrderByPerformedAtAsc(UUID instanceId);

    List<WorkFlowInstanceHistory> findByPerformedByIdOrderByPerformedAtDesc(UUID performedById);
}
