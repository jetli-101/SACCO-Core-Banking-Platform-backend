package com.example.sacco_core_banking.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.sacco_core_banking.entities.ChecklistStageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistStageLinkRepository extends JpaRepository<ChecklistStageLink, UUID> {
    List<ChecklistStageLink> findByChecklistId(UUID checklistId);

    List<ChecklistStageLink> findByStageId(UUID stageId);

    // Every link touching any stage of the given workflow — used to compute per-workflow
    // checklist coverage on the Analytics tab.
    List<ChecklistStageLink> findByStage_WorkFlow_Id(UUID workFlowId);

    Optional<ChecklistStageLink> findByChecklistIdAndStageId(UUID checklistId, UUID stageId);

    long countByChecklistId(UUID checklistId);

    void deleteByChecklistIdAndStageId(UUID checklistId, UUID stageId);

    void deleteByChecklistId(UUID checklistId);
}
