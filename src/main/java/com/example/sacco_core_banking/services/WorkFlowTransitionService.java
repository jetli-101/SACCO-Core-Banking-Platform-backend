package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowTransitionRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowTransitionResponse;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.entities.WorkFlowStatus;
import com.example.sacco_core_banking.entities.WorkFlowTransition;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStatusRepository;
import com.example.sacco_core_banking.repositories.WorkFlowTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD for the transitions between a workflow's stages — powers the create wizard's Connect
 * Components step and the Edit Workflow "Transitions" tab. Every method checks access to the
 * transition's parent workflow via WorkFlowService.assertCanAccess.
 */
@Service
@Transactional
public class WorkFlowTransitionService {

    @Autowired
    private WorkFlowTransitionRepository workFlowTransitionRepository;
    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowStatusRepository workFlowStatusRepository;

    public List<WorkFlowTransitionResponse> listTransitions(UUID workFlowId, User caller) {
        workFlowService.assertCanAccess(workFlowService.findWorkFlow(workFlowId), caller);
        return workFlowTransitionRepository.findByWorkFlowId(workFlowId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowTransitionResponse getTransitionById(UUID id, User caller) {
        WorkFlowTransition transition = findTransition(id);
        workFlowService.assertCanAccess(transition.getWorkFlow(), caller);
        return toResponse(transition);
    }

    public WorkFlowTransitionResponse createTransition(WorkFlowTransitionRequest request, User caller) {
        WorkFlow workFlow = workFlowService.findWorkFlow(request.getWorkFlowId());
        workFlowService.assertCanAccess(workFlow, caller);
        WorkFlowStage fromStage = findStage(workFlow.getId(), request.getFromStageId());
        WorkFlowStage toStage = findStage(workFlow.getId(), request.getToStageId());

        WorkFlowTransition transition = new WorkFlowTransition();
        transition.setWorkFlow(workFlow);
        transition.setFromStage(fromStage);
        transition.setToStage(toStage);
        applyRequest(transition, request, fromStage, toStage);

        return toResponse(workFlowTransitionRepository.save(transition));
    }

    public WorkFlowTransitionResponse updateTransition(UUID id, WorkFlowTransitionRequest request, User caller) {
        WorkFlowTransition transition = findTransition(id);
        workFlowService.assertCanAccess(transition.getWorkFlow(), caller);
        WorkFlowStage fromStage = findStage(transition.getWorkFlow().getId(), request.getFromStageId());
        WorkFlowStage toStage = findStage(transition.getWorkFlow().getId(), request.getToStageId());

        transition.setFromStage(fromStage);
        transition.setToStage(toStage);
        applyRequest(transition, request, fromStage, toStage);

        return toResponse(workFlowTransitionRepository.save(transition));
    }

    public void deleteTransition(UUID id, User caller) {
        WorkFlowTransition transition = findTransition(id);
        workFlowService.assertCanAccess(transition.getWorkFlow(), caller);
        workFlowTransitionRepository.delete(transition);
    }

    private WorkFlowTransition findTransition(UUID id) {
        return workFlowTransitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow transition not found"));
    }

    private WorkFlowStage findStage(UUID workFlowId, UUID stageId) {
        WorkFlowStage stage = workFlowStageRepository.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow stage not found"));
        if (!stage.getWorkFlow().getId().equals(workFlowId)) {
            throw new InvalidStateException("Stage does not belong to this workflow");
        }
        return stage;
    }

    private void applyRequest(WorkFlowTransition transition, WorkFlowTransitionRequest request,
                               WorkFlowStage fromStage, WorkFlowStage toStage) {
        if (fromStage.getId().equals(toStage.getId())) {
            throw new InvalidStateException("A transition cannot loop back to the same stage");
        }

        WorkFlowStatus resultingStatus = null;
        if (request.getResultingStatusId() != null) {
            resultingStatus = workFlowStatusRepository.findById(request.getResultingStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resulting status not found"));
        }

        transition.setName(request.getName());
        transition.setActionType(request.getActionType());
        transition.setResultingStatus(resultingStatus);
        transition.setDescription(request.getDescription());
        transition.setActive(request.isActive());
    }

    private WorkFlowTransitionResponse toResponse(WorkFlowTransition transition) {
        return WorkFlowTransitionResponse.builder()
                .id(transition.getId())
                .workFlowId(transition.getWorkFlow().getId())
                .fromStageId(transition.getFromStage().getId())
                .fromStageName(transition.getFromStage().getName())
                .toStageId(transition.getToStage().getId())
                .toStageName(transition.getToStage().getName())
                .name(transition.getName())
                .actionType(transition.getActionType())
                .resultingStatusId(transition.getResultingStatus() != null ? transition.getResultingStatus().getId() : null)
                .resultingStatusName(transition.getResultingStatus() != null ? transition.getResultingStatus().getName() : null)
                .description(transition.getDescription())
                .active(transition.isActive())
                .build();
    }
}
