package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.AvailableTransitionResponse;
import com.example.sacco_core_banking.dto.workflow.ReassignWorkFlowInstanceRequest;
import com.example.sacco_core_banking.dto.workflow.StartWorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.TransitionWorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionChecklistResponse;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowInstance;
import com.example.sacco_core_banking.entities.WorkFlowInstanceHistory;
import com.example.sacco_core_banking.entities.WorkFlowMapping;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.entities.WorkFlowStageAction;
import com.example.sacco_core_banking.entities.WorkFlowStageActionChecklist;
import com.example.sacco_core_banking.entities.WorkFlowTransition;
import com.example.sacco_core_banking.enums.RoleName;
import com.example.sacco_core_banking.enums.TransitionActionType;
import com.example.sacco_core_banking.enums.WorkFlowInstanceStatus;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserGroupMemberRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import com.example.sacco_core_banking.repositories.WorkFlowInstanceHistoryRepository;
import com.example.sacco_core_banking.repositories.WorkFlowInstanceRepository;
import com.example.sacco_core_banking.repositories.WorkFlowMappingRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageActionChecklistRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageActionRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageResponsibleUserRepository;
import com.example.sacco_core_banking.repositories.WorkFlowTransitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The reusable core any module plugs into: start a process against its registered
 * WorkFlowMapping, then move it stage-by-stage via transition(). Both operations carry the
 * instance's data payload along with them — startProcess seeds it, transition merges in
 * whatever changed at that step — so downstream stages always see the accumulated
 * business data, not just a reference id.
 *
 * transition() also enforces who may act on the current stage (responsibleType: GROUP/
 * ROLE/USERS/ANY, with a ROLE_SYSTEM_ADMINISTRATOR override) and, when a WorkFlowStageAction
 * exists for the transition being taken, its checklist/approval-comment gates.
 */
@Service
@Transactional
public class WorkFlowEngineService {

    @Autowired
    private WorkFlowMappingRepository workFlowMappingRepository;
    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowTransitionRepository workFlowTransitionRepository;
    @Autowired
    private WorkFlowInstanceRepository workFlowInstanceRepository;
    @Autowired
    private WorkFlowInstanceHistoryRepository workFlowInstanceHistoryRepository;
    @Autowired
    private WorkFlowStageActionRepository workFlowStageActionRepository;
    @Autowired
    private WorkFlowStageActionChecklistRepository workFlowStageActionChecklistRepository;
    @Autowired
    private WorkFlowStageResponsibleUserRepository workFlowStageResponsibleUserRepository;
    @Autowired
    private UserGroupMemberRepository userGroupMemberRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;

    private static final String ACTIVE_STATE_TEXT_ID = "ACTIVE";

    public WorkFlowInstanceResponse startProcess(StartWorkFlowRequest request, User initiator) {
        WorkFlowMapping mapping = workFlowMappingRepository.findByProcessTypeKeyAndActiveTrue(request.getProcessTypeKey())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No workflow is mapped to process type '" + request.getProcessTypeKey() + "'"));
        WorkFlow workFlow = mapping.getWorkFlow();
        if (!workFlow.isActive()) {
            throw new InvalidStateException("Workflow '" + workFlow.getName() + "' is not active");
        }

        WorkFlowStage initialStage = workFlowStageRepository.findFirstByWorkFlowIdAndIsInitialTrue(workFlow.getId())
                .orElseThrow(() -> new InvalidStateException(
                        "Workflow '" + workFlow.getName() + "' has no initial stage configured"));
        checkStageActive(initialStage);

        User assignedTo = null;
        if (request.getAssignedTo() != null) {
            assignedTo = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("Assigned user not found"));
        }

        OffsetDateTime now = OffsetDateTime.now();

        WorkFlowInstance instance = new WorkFlowInstance();
        instance.setProcessCode(generateProcessCode(request.getProcessTypeKey()));
        instance.setProcessName(request.getProcessName());
        instance.setWorkFlow(workFlow);
        instance.setProcessTypeKey(request.getProcessTypeKey());
        instance.setReferenceId(request.getReferenceId());
        instance.setCurrentStage(initialStage);
        instance.setCurrentStatus(initialStage.getDefaultStatus());
        instance.setStatus(WorkFlowInstanceStatus.INITIATED);
        instance.setPriority(request.getPriority());
        instance.setAssignedTo(assignedTo);
        instance.setDueDate(request.getDueDate());
        instance.setInitiatedBy(initiator);
        instance.setInitiatedAt(now);
        instance.setData(request.getData() != null ? new HashMap<>(request.getData()) : new HashMap<>());

        WorkFlowInstance saved = workFlowInstanceRepository.save(instance);

        WorkFlowInstanceHistory history = new WorkFlowInstanceHistory();
        history.setInstance(saved);
        history.setToStage(initialStage);
        history.setComment("Process started");
        history.setPerformedBy(initiator);
        history.setPerformedAt(now);
        history.setDataSnapshot(new HashMap<>(saved.getData()));
        workFlowInstanceHistoryRepository.save(history);

        return workFlowInstanceService.toResponse(saved);
    }

    public WorkFlowInstanceResponse transition(UUID instanceId, TransitionWorkFlowRequest request, User actor) {
        WorkFlowInstance instance = workFlowInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Process instance not found"));

        if (instance.getStatus() == WorkFlowInstanceStatus.COMPLETED
                || instance.getStatus() == WorkFlowInstanceStatus.REJECTED) {
            throw new InvalidStateException("This process has already ended and cannot be transitioned further");
        }

        WorkFlowTransition transition = workFlowTransitionRepository.findById(request.getTransitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transition not found"));

        if (!transition.isActive()) {
            throw new InvalidStateException("This transition is not active");
        }
        if (!transition.getFromStage().getId().equals(instance.getCurrentStage().getId())) {
            throw new InvalidStateException("This transition does not start from the process's current stage");
        }

        WorkFlowStage fromStage = instance.getCurrentStage();
        WorkFlowStage toStage = transition.getToStage();
        checkStageActive(toStage);
        checkAuthorized(fromStage, actor);
        checkGates(transition, request);

        Map<String, Object> mergedData = instance.getData() != null ? new HashMap<>(instance.getData()) : new HashMap<>();
        if (request.getDataChanges() != null) {
            mergedData.putAll(request.getDataChanges());
        }
        instance.setData(mergedData);

        instance.setCurrentStage(toStage);
        instance.setCurrentStatus(transition.getResultingStatus());
        instance.setStatus(resolveStatus(toStage, transition));

        WorkFlowInstance saved = workFlowInstanceRepository.save(instance);

        WorkFlowInstanceHistory history = new WorkFlowInstanceHistory();
        history.setInstance(saved);
        history.setFromStage(fromStage);
        history.setToStage(toStage);
        history.setTransition(transition);
        history.setComment(request.getComment());
        history.setPerformedBy(actor);
        history.setPerformedAt(OffsetDateTime.now());
        history.setDataSnapshot(new HashMap<>(mergedData));
        workFlowInstanceHistoryRepository.save(history);

        return workFlowInstanceService.toResponse(saved);
    }

    /**
     * Forward-to-person: reassigns an instance to a specific user at its current stage —
     * deliberately separate from transition(), since the stage/status don't change, only
     * who's responsible for acting next. Anyone currently authorized to act on the stage
     * (same rule as transition()) may forward it on.
     */
    public WorkFlowInstanceResponse reassign(UUID instanceId, ReassignWorkFlowInstanceRequest request, User actor) {
        WorkFlowInstance instance = workFlowInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Process instance not found"));

        if (instance.getStatus() == WorkFlowInstanceStatus.COMPLETED
                || instance.getStatus() == WorkFlowInstanceStatus.REJECTED) {
            throw new InvalidStateException("This process has already ended and cannot be forwarded");
        }

        WorkFlowStage currentStage = instance.getCurrentStage();
        checkAuthorized(currentStage, actor);

        User newAssignee = userRepository.findById(request.getAssignedToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User to forward to not found"));

        instance.setAssignedTo(newAssignee);
        WorkFlowInstance saved = workFlowInstanceRepository.save(instance);

        String comment = "Forwarded to " + newAssignee.getUsername()
                + (request.getComment() != null && !request.getComment().isBlank() ? ": " + request.getComment() : "");

        WorkFlowInstanceHistory history = new WorkFlowInstanceHistory();
        history.setInstance(saved);
        history.setFromStage(currentStage);
        history.setToStage(currentStage);
        history.setComment(comment);
        history.setPerformedBy(actor);
        history.setPerformedAt(OffsetDateTime.now());
        history.setDataSnapshot(new HashMap<>(saved.getData()));
        workFlowInstanceHistoryRepository.save(history);

        return workFlowInstanceService.toResponse(saved);
    }

    /** Transitions the frontend can offer from an instance's current stage, annotated with their gating rules. */
    public List<AvailableTransitionResponse> getAvailableTransitions(UUID instanceId) {
        WorkFlowInstance instance = workFlowInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Process instance not found"));

        return workFlowTransitionRepository
                .findByWorkFlowIdAndFromStageIdAndActiveTrue(instance.getWorkFlow().getId(), instance.getCurrentStage().getId())
                .stream()
                .map(this::toAvailableTransitionResponse)
                .collect(Collectors.toList());
    }

    private AvailableTransitionResponse toAvailableTransitionResponse(WorkFlowTransition transition) {
        WorkFlowStageAction action = workFlowStageActionRepository.findByTransitionId(transition.getId())
                .filter(WorkFlowStageAction::isActive)
                .orElse(null);

        List<WorkFlowStageActionChecklistResponse> checklist = action == null
                ? List.of()
                : workFlowStageActionChecklistRepository.findByStageActionIdOrderByOrderNoAsc(action.getId()).stream()
                        .map(item -> WorkFlowStageActionChecklistResponse.builder()
                                .id(item.getId())
                                .itemText(item.getItemText())
                                .required(item.isRequired())
                                .orderNo(item.getOrderNo())
                                .build())
                        .collect(Collectors.toList());

        return AvailableTransitionResponse.builder()
                .transitionId(transition.getId())
                .name(transition.getName())
                .actionType(transition.getActionType().name())
                .toStageId(transition.getToStage().getId())
                .toStageName(transition.getToStage().getName())
                .requiresChecklistCompletion(action != null && action.isRequiresChecklistCompletion())
                .requiresApprovalComment(action != null && action.isRequiresApprovalComment())
                .checklistItems(checklist)
                .build();
    }

    private void checkStageActive(WorkFlowStage stage) {
        if (!ACTIVE_STATE_TEXT_ID.equals(stage.getState().getTextId())) {
            throw new InvalidStateException("Stage '" + stage.getName() + "' is not active");
        }
    }

    private void checkAuthorized(WorkFlowStage stage, User actor) {
        if (isSystemAdministrator(actor)) {
            return;
        }
        boolean authorized = switch (stage.getResponsibleType()) {
            case GROUP -> stage.getResponsibleGroup() != null
                    && userGroupMemberRepository.findByUserIdAndUserGroupId(actor.getId(), stage.getResponsibleGroup().getId()).isPresent();
            case ROLE -> stage.getResponsibleRole() != null
                    && userRoleRepository.findByUserIdAndRoleId(actor.getId(), stage.getResponsibleRole().getId()).isPresent();
            case USERS -> workFlowStageResponsibleUserRepository.findByStageIdAndUserId(stage.getId(), actor.getId()).isPresent();
            case ANY -> true;
        };
        if (!authorized) {
            throw new InvalidStateException("You are not authorized to act on this stage");
        }
    }

    private boolean isSystemAdministrator(User actor) {
        return roleRepository.findByName(RoleName.ROLE_SYSTEM_ADMINISTRATOR.name())
                .map(role -> userRoleRepository.findByUserIdAndRoleId(actor.getId(), role.getId()).isPresent())
                .orElse(false);
    }

    private void checkGates(WorkFlowTransition transition, TransitionWorkFlowRequest request) {
        WorkFlowStageAction action = workFlowStageActionRepository.findByTransitionId(transition.getId())
                .filter(WorkFlowStageAction::isActive)
                .orElse(null);
        if (action == null) {
            return;
        }

        if (action.isRequiresApprovalComment() && (request.getComment() == null || request.getComment().isBlank())) {
            throw new InvalidStateException("A comment is required to take this action");
        }

        if (action.isRequiresChecklistCompletion()) {
            List<UUID> completed = request.getCompletedChecklistItemIds() != null
                    ? request.getCompletedChecklistItemIds()
                    : List.of();
            List<String> missing = workFlowStageActionChecklistRepository
                    .findByStageActionIdOrderByOrderNoAsc(action.getId()).stream()
                    .filter(WorkFlowStageActionChecklist::isRequired)
                    .filter(item -> !completed.contains(item.getId()))
                    .map(WorkFlowStageActionChecklist::getItemText)
                    .collect(Collectors.toList());
            if (!missing.isEmpty()) {
                throw new InvalidStateException("Checklist items not completed: " + String.join(", ", missing));
            }
        }
    }

    private WorkFlowInstanceStatus resolveStatus(WorkFlowStage toStage, WorkFlowTransition transition) {
        if (!toStage.isFinal()) {
            return WorkFlowInstanceStatus.IN_PROGRESS;
        }
        return transition.getActionType() == TransitionActionType.REJECTION
                ? WorkFlowInstanceStatus.REJECTED
                : WorkFlowInstanceStatus.COMPLETED;
    }

    private String generateProcessCode(String processTypeKey) {
        String prefix = processTypeKey.length() >= 3 ? processTypeKey.substring(0, 3) : processTypeKey;
        return (prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12)).toUpperCase();
    }
}
