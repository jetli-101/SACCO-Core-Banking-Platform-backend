package com.example.sacco_core_banking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageResponse;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserGroup;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.entities.WorkFlowStageResponsibleUser;
import com.example.sacco_core_banking.entities.WorkFlowState;
import com.example.sacco_core_banking.entities.WorkFlowStatus;
import com.example.sacco_core_banking.enums.StageResponsibleType;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserGroupRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.WorkFlowRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageResponsibleUserRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStateRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for the stages of a workflow — powers the create wizard's Stage Structure step and the Edit Workflow "Stages" tab. */
@Service
@Transactional
public class WorkFlowStageService {

    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowRepository workFlowRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkFlowStateRepository workFlowStateRepository;
    @Autowired
    private WorkFlowStatusRepository workFlowStatusRepository;
    @Autowired
    private WorkFlowStageResponsibleUserRepository workFlowStageResponsibleUserRepository;

    public List<WorkFlowStageResponse> listStages(UUID workFlowId) {
        return workFlowStageRepository.findByWorkFlowIdOrderByOrderNoAsc(workFlowId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public WorkFlowStageResponse getStageById(UUID id) {
        return toResponse(findStage(id));
    }

    public WorkFlowStageResponse createStage(WorkFlowStageRequest request) {
        WorkFlow workFlow = workFlowRepository.findById(request.getWorkFlowId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));

        WorkFlowStage stage = new WorkFlowStage();
        stage.setWorkFlow(workFlow);
        applyRequest(stage, request);

        WorkFlowStage saved = workFlowStageRepository.save(stage);
        replaceResponsibleUsers(saved, request);

        return toResponse(saved);
    }

    public WorkFlowStageResponse updateStage(UUID id, WorkFlowStageRequest request) {
        WorkFlowStage stage = findStage(id);
        applyRequest(stage, request);

        WorkFlowStage saved = workFlowStageRepository.save(stage);
        replaceResponsibleUsers(saved, request);

        return toResponse(saved);
    }

    public void deleteStage(UUID id) {
        WorkFlowStage stage = findStage(id);
        workFlowStageResponsibleUserRepository.deleteByStageId(stage.getId());
        workFlowStageRepository.delete(stage);
    }

    private WorkFlowStage findStage(UUID id) {
        return workFlowStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow stage not found"));
    }

    private void applyRequest(WorkFlowStage stage, WorkFlowStageRequest request) {
        WorkFlowState state = workFlowStateRepository.findById(request.getStateId())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow state not found"));

        WorkFlowStatus defaultStatus = null;
        if (request.getDefaultStatusId() != null) {
            defaultStatus = workFlowStatusRepository.findById(request.getDefaultStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Default status not found"));
        }

        UserGroup responsibleGroup = null;
        Role responsibleRole = null;
        switch (request.getResponsibleType()) {
            case GROUP -> {
                if (request.getResponsibleGroupId() == null) {
                    throw new InvalidStateException("responsibleGroupId is required when responsibleType is GROUP");
                }
                responsibleGroup = userGroupRepository.findById(request.getResponsibleGroupId())
                        .orElseThrow(() -> new ResourceNotFoundException("Responsible group not found"));
            }
            case ROLE -> {
                if (request.getResponsibleRoleId() == null) {
                    throw new InvalidStateException("responsibleRoleId is required when responsibleType is ROLE");
                }
                responsibleRole = roleRepository.findById(request.getResponsibleRoleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Responsible role not found"));
            }
            case USERS -> {
                if (request.getResponsibleUserIds() == null || request.getResponsibleUserIds().isEmpty()) {
                    throw new InvalidStateException("responsibleUserIds is required when responsibleType is USERS");
                }
            }
            case ANY -> {
                // no target to resolve
            }
        }

        stage.setName(request.getName());
        stage.setDescription(request.getDescription());
        stage.setOrderNo(request.getOrderNo());
        stage.setResponsibleType(request.getResponsibleType());
        stage.setResponsibleGroup(responsibleGroup);
        stage.setResponsibleRole(responsibleRole);
        stage.setState(state);
        stage.setDefaultStatus(defaultStatus);
        stage.setInitial(request.isInitial());
        stage.setFinal(request.isFinal());
        stage.setInterfaceKey(request.getInterfaceKey());
    }

    private void replaceResponsibleUsers(WorkFlowStage stage, WorkFlowStageRequest request) {
        workFlowStageResponsibleUserRepository.deleteByStageId(stage.getId());

        if (stage.getResponsibleType() != StageResponsibleType.USERS || request.getResponsibleUserIds() == null) {
            return;
        }
        for (UUID userId : request.getResponsibleUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Responsible user not found: " + userId));
            workFlowStageResponsibleUserRepository.save(new WorkFlowStageResponsibleUser(stage, user));
        }
    }

    private WorkFlowStageResponse toResponse(WorkFlowStage stage) {
        List<UUID> responsibleUserIds = workFlowStageResponsibleUserRepository.findByStageId(stage.getId()).stream()
                .map(link -> link.getUser().getId())
                .collect(Collectors.toCollection(ArrayList::new));

        return WorkFlowStageResponse.builder()
                .id(stage.getId())
                .workFlowId(stage.getWorkFlow().getId())
                .name(stage.getName())
                .description(stage.getDescription())
                .orderNo(stage.getOrderNo())
                .responsibleType(stage.getResponsibleType())
                .responsibleGroupId(stage.getResponsibleGroup() != null ? stage.getResponsibleGroup().getId() : null)
                .responsibleGroupName(stage.getResponsibleGroup() != null ? stage.getResponsibleGroup().getName() : null)
                .responsibleRoleId(stage.getResponsibleRole() != null ? stage.getResponsibleRole().getId() : null)
                .responsibleRoleName(stage.getResponsibleRole() != null ? stage.getResponsibleRole().getName() : null)
                .responsibleUserIds(responsibleUserIds)
                .stateId(stage.getState().getId())
                .stateName(stage.getState().getName())
                .defaultStatusId(stage.getDefaultStatus() != null ? stage.getDefaultStatus().getId() : null)
                .defaultStatusName(stage.getDefaultStatus() != null ? stage.getDefaultStatus().getName() : null)
                .isInitial(stage.isInitial())
                .isFinal(stage.isFinal())
                .interfaceKey(stage.getInterfaceKey())
                .build();
    }
}
