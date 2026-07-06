package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceHistoryResponse;
import com.example.sacco_core_banking.entities.WorkFlowInstanceHistory;
import com.example.sacco_core_banking.repositories.WorkFlowInstanceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read-only audit trail for a process instance — one entry per transition taken. */
@Service
@Transactional
public class WorkFlowInstanceHistoryService {

    @Autowired
    private WorkFlowInstanceHistoryRepository workFlowInstanceHistoryRepository;

    public List<WorkFlowInstanceHistoryResponse> listForInstance(UUID instanceId) {
        return workFlowInstanceHistoryRepository.findByInstanceIdOrderByPerformedAtAsc(instanceId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WorkFlowInstanceHistoryResponse toResponse(WorkFlowInstanceHistory history) {
        return WorkFlowInstanceHistoryResponse.builder()
                .id(history.getId())
                .fromStageId(history.getFromStage() != null ? history.getFromStage().getId() : null)
                .fromStageName(history.getFromStage() != null ? history.getFromStage().getName() : null)
                .toStageId(history.getToStage().getId())
                .toStageName(history.getToStage().getName())
                .transitionId(history.getTransition() != null ? history.getTransition().getId() : null)
                .transitionName(history.getTransition() != null ? history.getTransition().getName() : null)
                .comment(history.getComment())
                .performedByUserId(history.getPerformedBy() != null ? history.getPerformedBy().getId() : null)
                .performedByName(history.getPerformedBy() != null ? history.getPerformedBy().getUsername() : null)
                .performedAt(history.getPerformedAt())
                .dataSnapshot(history.getDataSnapshot())
                .build();
    }
}
