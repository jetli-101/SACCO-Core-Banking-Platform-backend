package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowDashboardResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowResponse;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.enums.WorkFlowInstanceStatus;
import com.example.sacco_core_banking.repositories.WorkFlowInstanceRepository;
import com.example.sacco_core_banking.repositories.WorkFlowRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD for the workflow definition itself — powers the Workflows list, the create wizard's
 * Core Components step, and the Workflow Details tab. Stages/transitions have their own
 * services/controllers; process execution lives in WorkFlowEngineService.
 */
@Service
@Transactional
public class WorkFlowService {

    @Autowired
    private WorkFlowRepository workFlowRepository;
    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowInstanceRepository workFlowInstanceRepository;

    private static final List<WorkFlowInstanceStatus> ACTIVE_STATUSES =
            List.of(WorkFlowInstanceStatus.INITIATED, WorkFlowInstanceStatus.IN_PROGRESS);

    public List<WorkFlowResponse> listWorkFlows() {
        return workFlowRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowResponse getWorkFlowById(UUID id) {
        return toResponse(findWorkFlow(id));
    }

    public WorkFlowDashboardResponse getDashboard() {
        long totalWorkFlows = workFlowRepository.count();
        long activeWorkFlows = workFlowRepository.findAll().stream().filter(WorkFlow::isActive).count();
        long activeProcesses = workFlowInstanceRepository.countByStatusIn(ACTIVE_STATUSES);
        double averageStages = totalWorkFlows == 0 ? 0 : (double) workFlowStageRepository.count() / totalWorkFlows;

        return WorkFlowDashboardResponse.builder()
                .totalWorkFlows(totalWorkFlows)
                .activeWorkFlows(activeWorkFlows)
                .activeProcesses(activeProcesses)
                .averageStages(averageStages)
                .build();
    }

    public WorkFlowResponse createWorkFlow(WorkFlowRequest request, User creator) {
        workFlowRepository.findByName(request.getName()).ifPresent(existing -> {
            throw new DuplicateResourceException("A workflow with this name already exists");
        });

        WorkFlow workFlow = new WorkFlow();
        workFlow.setName(request.getName());
        workFlow.setDescription(request.getDescription());
        workFlow.setActive(request.isActive());
        workFlow.setCreatedBy(creator);

        return toResponse(workFlowRepository.save(workFlow));
    }

    public WorkFlowResponse updateWorkFlow(UUID id, WorkFlowRequest request) {
        WorkFlow workFlow = findWorkFlow(id);

        workFlowRepository.findByName(request.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A workflow with this name already exists");
                });

        workFlow.setName(request.getName());
        workFlow.setDescription(request.getDescription());
        workFlow.setActive(request.isActive());

        return toResponse(workFlowRepository.save(workFlow));
    }

    public void deleteWorkFlow(UUID id) {
        WorkFlow workFlow = findWorkFlow(id);
        workFlowRepository.delete(workFlow);
    }

    private WorkFlow findWorkFlow(UUID id) {
        return workFlowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));
    }

    private WorkFlowResponse toResponse(WorkFlow workFlow) {
        List<WorkFlowStage> stages = workFlowStageRepository.findByWorkFlowIdOrderByOrderNoAsc(workFlow.getId());
        WorkFlowStage initialStage = stages.stream().filter(WorkFlowStage::isInitial).findFirst().orElse(null);

        return WorkFlowResponse.builder()
                .id(workFlow.getId())
                .name(workFlow.getName())
                .description(workFlow.getDescription())
                .active(workFlow.isActive())
                .createdByUserId(workFlow.getCreatedBy() != null ? workFlow.getCreatedBy().getId() : null)
                .createdByName(workFlow.getCreatedBy() != null ? workFlow.getCreatedBy().getUsername() : null)
                .totalStages(stages.size())
                .initialStageId(initialStage != null ? initialStage.getId() : null)
                .initialStageName(initialStage != null ? initialStage.getName() : null)
                .createdAt(workFlow.getCreatedAt())
                .updatedAt(workFlow.getUpdatedAt())
                .build();
    }
}
