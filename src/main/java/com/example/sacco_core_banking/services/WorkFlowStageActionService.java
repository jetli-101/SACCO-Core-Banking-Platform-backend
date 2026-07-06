package com.example.sacco_core_banking.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionChecklistRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionChecklistResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionResponse;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.entities.WorkFlowStageAction;
import com.example.sacco_core_banking.entities.WorkFlowStageActionChecklist;
import com.example.sacco_core_banking.entities.WorkFlowTransition;
import com.example.sacco_core_banking.repositories.WorkFlowStageActionChecklistRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageActionRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import com.example.sacco_core_banking.repositories.WorkFlowTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD for the optional gating layer on top of a transition — a WorkFlowStageAction plus
 * its checklist items. WorkFlowEngineService reads these (by transitionId) when a process
 * takes that transition; a transition with no action here is ungated.
 */
@Service
@Transactional
public class WorkFlowStageActionService {

    @Autowired
    private WorkFlowStageActionRepository workFlowStageActionRepository;
    @Autowired
    private WorkFlowStageActionChecklistRepository workFlowStageActionChecklistRepository;
    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowTransitionRepository workFlowTransitionRepository;

    public List<WorkFlowStageActionResponse> listActions(UUID stageId) {
        return workFlowStageActionRepository.findByStageId(stageId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowStageActionResponse createAction(WorkFlowStageActionRequest request) {
        WorkFlowStage stage = workFlowStageRepository.findById(request.getStageId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow stage not found"));
        WorkFlowTransition transition = workFlowTransitionRepository.findById(request.getTransitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow transition not found"));

        workFlowStageActionRepository.findByTransitionId(transition.getId()).ifPresent(existing -> {
            throw new DuplicateResourceException("This transition already has a stage action");
        });
        if (!transition.getFromStage().getId().equals(stage.getId())) {
            throw new InvalidStateException("Transition does not start from the given stage");
        }

        WorkFlowStageAction action = new WorkFlowStageAction();
        action.setStage(stage);
        action.setTransition(transition);
        applyRequest(action, request);

        WorkFlowStageAction saved = workFlowStageActionRepository.save(action);
        replaceChecklist(saved, request.getChecklistItems());

        return toResponse(saved);
    }

    public WorkFlowStageActionResponse updateAction(UUID id, WorkFlowStageActionRequest request) {
        WorkFlowStageAction action = findAction(id);
        applyRequest(action, request);

        WorkFlowStageAction saved = workFlowStageActionRepository.save(action);
        replaceChecklist(saved, request.getChecklistItems());

        return toResponse(saved);
    }

    public void deleteAction(UUID id) {
        WorkFlowStageAction action = findAction(id);
        workFlowStageActionRepository.delete(action);
    }

    private WorkFlowStageAction findAction(UUID id) {
        return workFlowStageActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stage action not found"));
    }

    private void applyRequest(WorkFlowStageAction action, WorkFlowStageActionRequest request) {
        action.setName(request.getName());
        action.setRequiresChecklistCompletion(request.isRequiresChecklistCompletion());
        action.setRequiresApprovalComment(request.isRequiresApprovalComment());
        action.setActive(request.isActive());
    }

    private void replaceChecklist(WorkFlowStageAction action, List<WorkFlowStageActionChecklistRequest> items) {
        workFlowStageActionChecklistRepository.findByStageActionIdOrderByOrderNoAsc(action.getId())
                .forEach(workFlowStageActionChecklistRepository::delete);

        if (items == null) {
            return;
        }
        for (WorkFlowStageActionChecklistRequest itemRequest : items) {
            WorkFlowStageActionChecklist item = new WorkFlowStageActionChecklist();
            item.setStageAction(action);
            item.setItemText(itemRequest.getItemText());
            item.setRequired(itemRequest.isRequired());
            item.setOrderNo(itemRequest.getOrderNo());
            workFlowStageActionChecklistRepository.save(item);
        }
    }

    private WorkFlowStageActionResponse toResponse(WorkFlowStageAction action) {
        List<WorkFlowStageActionChecklistResponse> checklist = workFlowStageActionChecklistRepository
                .findByStageActionIdOrderByOrderNoAsc(action.getId()).stream()
                .sorted(Comparator.comparingInt(WorkFlowStageActionChecklist::getOrderNo))
                .map(item -> WorkFlowStageActionChecklistResponse.builder()
                        .id(item.getId())
                        .itemText(item.getItemText())
                        .required(item.isRequired())
                        .orderNo(item.getOrderNo())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        return WorkFlowStageActionResponse.builder()
                .id(action.getId())
                .stageId(action.getStage().getId())
                .transitionId(action.getTransition().getId())
                .transitionName(action.getTransition().getName())
                .name(action.getName())
                .requiresChecklistCompletion(action.isRequiresChecklistCompletion())
                .requiresApprovalComment(action.isRequiresApprovalComment())
                .active(action.isActive())
                .checklistItems(checklist)
                .build();
    }
}
