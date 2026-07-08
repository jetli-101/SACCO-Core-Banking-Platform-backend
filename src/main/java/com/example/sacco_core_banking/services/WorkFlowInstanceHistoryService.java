package com.example.sacco_core_banking.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceHistoryResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowOutTrayItemResponse;
import com.example.sacco_core_banking.entities.WorkFlowInstance;
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

    /**
     * The caller's Out-Tray: one row per instance they've personally acted on, carrying their
     * most recent action on it. History is fetched newest-first so the first entry seen per
     * instance id is the one kept.
     */
    public List<WorkFlowOutTrayItemResponse> listOutTrayForUser(UUID userId) {
        Map<UUID, WorkFlowInstanceHistory> latestByInstance = new LinkedHashMap<>();
        for (WorkFlowInstanceHistory history : workFlowInstanceHistoryRepository.findByPerformedByIdOrderByPerformedAtDesc(userId)) {
            latestByInstance.putIfAbsent(history.getInstance().getId(), history);
        }
        return latestByInstance.values().stream()
                .map(this::toOutTrayResponse)
                .collect(Collectors.toList());
    }

    private WorkFlowOutTrayItemResponse toOutTrayResponse(WorkFlowInstanceHistory history) {
        WorkFlowInstance instance = history.getInstance();
        return WorkFlowOutTrayItemResponse.builder()
                .instanceId(instance.getId())
                .processCode(instance.getProcessCode())
                .processName(instance.getProcessName())
                .workFlowId(instance.getWorkFlow().getId())
                .workFlowName(instance.getWorkFlow().getName())
                .referenceId(instance.getReferenceId())
                .currentStageName(instance.getCurrentStage().getName())
                .status(instance.getStatus().name())
                .priority(instance.getPriority().name())
                .dueDate(instance.getDueDate())
                .initiatedAt(instance.getInitiatedAt())
                .historyId(history.getId())
                .fromStageName(history.getFromStage() != null ? history.getFromStage().getName() : null)
                .toStageName(history.getToStage().getName())
                .transitionName(history.getTransition() != null ? history.getTransition().getName() : null)
                .comment(history.getComment())
                .performedAt(history.getPerformedAt())
                .build();
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
