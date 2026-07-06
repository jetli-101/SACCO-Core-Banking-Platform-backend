package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.entities.WorkFlowStageActionChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowStageActionChecklistRepository extends JpaRepository<WorkFlowStageActionChecklist, UUID> {
    List<WorkFlowStageActionChecklist> findByStageActionIdOrderByOrderNoAsc(UUID stageActionId);
}
