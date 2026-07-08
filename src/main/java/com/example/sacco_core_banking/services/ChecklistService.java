package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.workflow.ChecklistAnalyticsResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistItemRequest;
import com.example.sacco_core_banking.dto.workflow.ChecklistItemResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistRequest;
import com.example.sacco_core_banking.dto.workflow.ChecklistResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistStageUsageResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistSummaryResponse;
import com.example.sacco_core_banking.dto.workflow.StageChecklistLinksResponse;
import com.example.sacco_core_banking.dto.workflow.WorkflowChecklistCoverageResponse;
import com.example.sacco_core_banking.entities.Checklist;
import com.example.sacco_core_banking.entities.ChecklistItem;
import com.example.sacco_core_banking.entities.ChecklistStageLink;
import com.example.sacco_core_banking.entities.WorkFlow;
import com.example.sacco_core_banking.entities.WorkFlowStage;
import com.example.sacco_core_banking.repositories.ChecklistItemRepository;
import com.example.sacco_core_banking.repositories.ChecklistRepository;
import com.example.sacco_core_banking.repositories.ChecklistStageLinkRepository;
import com.example.sacco_core_banking.repositories.WorkFlowRepository;
import com.example.sacco_core_banking.repositories.WorkFlowStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CRUD for standalone, reusable Checklists plus their many-to-many links to workflow
 * stages. Distinct from WorkFlowStageActionService, which gates a single transition —
 * a Checklist here is created once and can be linked to any number of stages across any
 * number of workflows (see the Workflow Checklist Linking screen).
 */
@Service
@Transactional
public class ChecklistService {

    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private ChecklistItemRepository checklistItemRepository;
    @Autowired
    private ChecklistStageLinkRepository checklistStageLinkRepository;
    @Autowired
    private WorkFlowStageRepository workFlowStageRepository;
    @Autowired
    private WorkFlowRepository workFlowRepository;

    public List<ChecklistResponse> listChecklists() {
        return checklistRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ChecklistResponse getChecklist(UUID id) {
        return toResponse(findChecklist(id));
    }

    public ChecklistResponse createChecklist(ChecklistRequest request) {
        Checklist checklist = new Checklist();
        applyRequest(checklist, request);
        return toResponse(checklistRepository.save(checklist));
    }

    public ChecklistResponse updateChecklist(UUID id, ChecklistRequest request) {
        Checklist checklist = findChecklist(id);
        applyRequest(checklist, request);
        return toResponse(checklistRepository.save(checklist));
    }

    public void deleteChecklist(UUID id) {
        Checklist checklist = findChecklist(id);
        checklistStageLinkRepository.deleteByChecklistId(id);
        checklistRepository.delete(checklist);
    }

    public ChecklistResponse addItem(UUID checklistId, ChecklistItemRequest request) {
        Checklist checklist = findChecklist(checklistId);
        ChecklistItem item = new ChecklistItem();
        item.setChecklist(checklist);
        item.setItemText(request.getItemText());
        item.setRequired(request.isRequired());
        item.setOrderNo(request.getOrderNo());
        checklistItemRepository.save(item);
        return toResponse(checklist);
    }

    public ChecklistResponse updateItem(UUID checklistId, UUID itemId, ChecklistItemRequest request) {
        ChecklistItem item = findItem(checklistId, itemId);
        item.setItemText(request.getItemText());
        item.setRequired(request.isRequired());
        item.setOrderNo(request.getOrderNo());
        checklistItemRepository.save(item);
        return toResponse(item.getChecklist());
    }

    public ChecklistResponse deleteItem(UUID checklistId, UUID itemId) {
        ChecklistItem item = findItem(checklistId, itemId);
        Checklist checklist = item.getChecklist();
        checklistItemRepository.delete(item);
        return toResponse(checklist);
    }

    public List<ChecklistStageUsageResponse> listUsages(UUID checklistId) {
        findChecklist(checklistId);
        return checklistStageLinkRepository.findByChecklistId(checklistId).stream()
                .map(link -> {
                    WorkFlowStage stage = link.getStage();
                    return ChecklistStageUsageResponse.builder()
                            .stageId(stage.getId())
                            .stageName(stage.getName())
                            .stageOrderNo(stage.getOrderNo())
                            .workFlowId(stage.getWorkFlow().getId())
                            .workFlowName(stage.getWorkFlow().getName())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void linkToStage(UUID checklistId, UUID stageId) {
        Checklist checklist = findChecklist(checklistId);
        WorkFlowStage stage = workFlowStageRepository.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow stage not found"));
        checklistStageLinkRepository.findByChecklistIdAndStageId(checklistId, stageId).ifPresent(existing -> {
            throw new DuplicateResourceException("This checklist is already linked to this stage");
        });
        checklistStageLinkRepository.save(new ChecklistStageLink(checklist, stage));
    }

    public void unlinkFromStage(UUID checklistId, UUID stageId) {
        checklistStageLinkRepository.deleteByChecklistIdAndStageId(checklistId, stageId);
    }

    public List<StageChecklistLinksResponse> listWorkflowLinks(UUID workFlowId) {
        return workFlowStageRepository.findByWorkFlowIdOrderByOrderNoAsc(workFlowId).stream()
                .map(stage -> {
                    List<ChecklistSummaryResponse> checklists = checklistStageLinkRepository.findByStageId(stage.getId()).stream()
                            .map(link -> toSummary(link.getChecklist()))
                            .collect(Collectors.toList());
                    return StageChecklistLinksResponse.builder()
                            .stageId(stage.getId())
                            .stageName(stage.getName())
                            .orderNo(stage.getOrderNo())
                            .checklists(checklists)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public ChecklistAnalyticsResponse getAnalytics() {
        List<WorkFlow> workflows = workFlowRepository.findAll();

        List<WorkflowChecklistCoverageResponse> perWorkflow = workflows.stream()
                .map(workflow -> {
                    Map<UUID, Checklist> distinctChecklists = checklistStageLinkRepository.findByStage_WorkFlow_Id(workflow.getId()).stream()
                            .map(ChecklistStageLink::getChecklist)
                            .collect(Collectors.toMap(Checklist::getId, checklist -> checklist, (a, b) -> a));

                    int itemCount = 0;
                    int mandatoryCount = 0;
                    for (Checklist checklist : distinctChecklists.values()) {
                        itemCount += checklistItemRepository.countByChecklistId(checklist.getId());
                        mandatoryCount += checklistItemRepository.countByChecklistIdAndRequiredTrue(checklist.getId());
                    }

                    return WorkflowChecklistCoverageResponse.builder()
                            .workFlowId(workflow.getId())
                            .workFlowName(workflow.getName())
                            .checklistCount(distinctChecklists.size())
                            .itemCount(itemCount)
                            .mandatoryCount(mandatoryCount)
                            .optionalCount(itemCount - mandatoryCount)
                            .build();
                })
                .collect(Collectors.toList());

        return ChecklistAnalyticsResponse.builder()
                .totalWorkflows(workFlowRepository.count())
                .totalStages(workFlowStageRepository.count())
                .totalChecklists(checklistRepository.count())
                .totalChecklistItems(checklistItemRepository.count())
                .totalMandatoryItems(checklistItemRepository.countByRequiredTrue())
                .perWorkflow(perWorkflow)
                .build();
    }

    private Checklist findChecklist(UUID id) {
        return checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist not found"));
    }

    private ChecklistItem findItem(UUID checklistId, UUID itemId) {
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));
        if (!item.getChecklist().getId().equals(checklistId)) {
            throw new InvalidStateException("Item does not belong to this checklist");
        }
        return item;
    }

    private void applyRequest(Checklist checklist, ChecklistRequest request) {
        checklist.setName(request.getName());
        checklist.setDescription(request.getDescription());
        checklist.setActive(request.isActive());
    }

    private ChecklistSummaryResponse toSummary(Checklist checklist) {
        return ChecklistSummaryResponse.builder()
                .id(checklist.getId())
                .name(checklist.getName())
                .itemCount((int) checklistItemRepository.countByChecklistId(checklist.getId()))
                .mandatoryCount((int) checklistItemRepository.countByChecklistIdAndRequiredTrue(checklist.getId()))
                .build();
    }

    private ChecklistResponse toResponse(Checklist checklist) {
        List<ChecklistItemResponse> items = checklistItemRepository.findByChecklistIdOrderByOrderNoAsc(checklist.getId()).stream()
                .map(item -> ChecklistItemResponse.builder()
                        .id(item.getId())
                        .checklistId(checklist.getId())
                        .itemText(item.getItemText())
                        .required(item.isRequired())
                        .orderNo(item.getOrderNo())
                        .build())
                .collect(Collectors.toList());

        int mandatoryItems = (int) items.stream().filter(ChecklistItemResponse::isRequired).count();

        return ChecklistResponse.builder()
                .id(checklist.getId())
                .name(checklist.getName())
                .description(checklist.getDescription())
                .active(checklist.isActive())
                .items(items)
                .totalItems(items.size())
                .mandatoryItems(mandatoryItems)
                .usedInStagesCount((int) checklistStageLinkRepository.countByChecklistId(checklist.getId()))
                .createdAt(checklist.getCreatedAt())
                .updatedAt(checklist.getUpdatedAt())
                .build();
    }
}
