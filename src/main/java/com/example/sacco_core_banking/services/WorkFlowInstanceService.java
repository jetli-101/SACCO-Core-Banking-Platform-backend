package com.example.sacco_core_banking.services;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceDashboardResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceResponse;
import com.example.sacco_core_banking.entities.WorkFlowInstance;
import com.example.sacco_core_banking.enums.WorkFlowInstanceStatus;
import com.example.sacco_core_banking.enums.WorkFlowPriority;
import com.example.sacco_core_banking.repositories.WorkFlowInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of running processes — powers the Workflow Instances list and its dashboard stat cards. */
@Service
@Transactional
public class WorkFlowInstanceService {

    @Autowired
    private WorkFlowInstanceRepository workFlowInstanceRepository;

    private static final List<WorkFlowInstanceStatus> ACTIVE_STATUSES =
            List.of(WorkFlowInstanceStatus.INITIATED, WorkFlowInstanceStatus.IN_PROGRESS);

    public List<WorkFlowInstanceResponse> listInstances() {
        return workFlowInstanceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowInstanceResponse getInstanceById(UUID id) {
        return toResponse(findInstance(id));
    }

    public WorkFlowInstanceDashboardResponse getDashboard() {
        OffsetDateTime now = OffsetDateTime.now();

        return WorkFlowInstanceDashboardResponse.builder()
                .activeProcesses(workFlowInstanceRepository.countByStatusIn(ACTIVE_STATUSES))
                .highPriority(workFlowInstanceRepository.countByPriorityAndStatusIn(WorkFlowPriority.HIGH, ACTIVE_STATUSES))
                .dueThisWeek(workFlowInstanceRepository.countByDueDateBetweenAndStatusIn(now, now.plusDays(7), ACTIVE_STATUSES))
                .rejected(workFlowInstanceRepository.countByStatus(WorkFlowInstanceStatus.REJECTED))
                .build();
    }

    WorkFlowInstance findInstance(UUID id) {
        return workFlowInstanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Process instance not found"));
    }

    /** Shared response mapping — also used by WorkFlowEngineService after starting/transitioning an instance. */
    public WorkFlowInstanceResponse toResponse(WorkFlowInstance instance) {
        Long daysRemaining = instance.getDueDate() != null
                ? Duration.between(OffsetDateTime.now(), instance.getDueDate()).toDays()
                : null;

        return WorkFlowInstanceResponse.builder()
                .id(instance.getId())
                .processCode(instance.getProcessCode())
                .processName(instance.getProcessName())
                .workFlowId(instance.getWorkFlow().getId())
                .workFlowName(instance.getWorkFlow().getName())
                .processTypeKey(instance.getProcessTypeKey())
                .referenceId(instance.getReferenceId())
                .currentStageId(instance.getCurrentStage().getId())
                .currentStageName(instance.getCurrentStage().getName())
                .currentStatusId(instance.getCurrentStatus() != null ? instance.getCurrentStatus().getId() : null)
                .currentStatusName(instance.getCurrentStatus() != null ? instance.getCurrentStatus().getName() : null)
                .status(instance.getStatus().name())
                .priority(instance.getPriority().name())
                .assignedToUserId(instance.getAssignedTo() != null ? instance.getAssignedTo().getId() : null)
                .assignedToName(instance.getAssignedTo() != null ? instance.getAssignedTo().getUsername() : null)
                .dueDate(instance.getDueDate())
                .daysRemaining(daysRemaining)
                .initiatedByUserId(instance.getInitiatedBy() != null ? instance.getInitiatedBy().getId() : null)
                .initiatedByName(instance.getInitiatedBy() != null ? instance.getInitiatedBy().getUsername() : null)
                .initiatedAt(instance.getInitiatedAt())
                .data(instance.getData())
                .build();
    }
}
