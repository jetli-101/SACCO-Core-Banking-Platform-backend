package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistAnalyticsResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistItemRequest;
import com.example.sacco_core_banking.dto.workflow.ChecklistRequest;
import com.example.sacco_core_banking.dto.workflow.ChecklistResponse;
import com.example.sacco_core_banking.dto.workflow.ChecklistStageUsageResponse;
import com.example.sacco_core_banking.dto.workflow.StageChecklistLinksResponse;
import com.example.sacco_core_banking.services.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Admin CRUD for standalone, reusable Checklists and their links to workflow stages. */
@RestController
@RequestMapping(path = Constants.CHECKLISTS_PATH)
@Tag(name = "Checklist Management", description = "APIs for managing reusable checklists and their workflow-stage links")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class ChecklistController {

    @Autowired
    private ChecklistService checklistService;

    @GetMapping
    @Operation(summary = "List all checklists")
    public ResponseEntity<ApiResponse<List<ChecklistResponse>>> listChecklists() {
        return ResponseEntity.ok(ApiResponse.success(checklistService.listChecklists(), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a checklist by id")
    public ResponseEntity<ApiResponse<ChecklistResponse>> getChecklist(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.getChecklist(id), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a checklist")
    public ResponseEntity<ApiResponse<ChecklistResponse>> createChecklist(@Valid @RequestBody ChecklistRequest request) {
        ChecklistResponse created = checklistService.createChecklist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Checklist created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a checklist")
    public ResponseEntity<ApiResponse<ChecklistResponse>> updateChecklist(@PathVariable UUID id, @Valid @RequestBody ChecklistRequest request) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.updateChecklist(id, request), "Checklist updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a checklist")
    public ResponseEntity<ApiResponse<Void>> deleteChecklist(@PathVariable UUID id) {
        checklistService.deleteChecklist(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Checklist deleted successfully"));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add an item to a checklist")
    public ResponseEntity<ApiResponse<ChecklistResponse>> addItem(@PathVariable UUID id, @Valid @RequestBody ChecklistItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(checklistService.addItem(id, request), "Checklist item added successfully"));
    }

    @PutMapping("/{id}/items/{itemId}")
    @Operation(summary = "Update a checklist item")
    public ResponseEntity<ApiResponse<ChecklistResponse>> updateItem(
            @PathVariable UUID id, @PathVariable UUID itemId, @Valid @RequestBody ChecklistItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.updateItem(id, itemId, request), "Checklist item updated successfully"));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Delete a checklist item")
    public ResponseEntity<ApiResponse<ChecklistResponse>> deleteItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.deleteItem(id, itemId), "Checklist item deleted successfully"));
    }

    @GetMapping("/{id}/usages")
    @Operation(summary = "List the stages a checklist is linked to")
    public ResponseEntity<ApiResponse<List<ChecklistStageUsageResponse>>> listUsages(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.listUsages(id), "success"));
    }

    @PostMapping("/{id}/stages/{stageId}")
    @Operation(summary = "Link a checklist to a workflow stage")
    public ResponseEntity<ApiResponse<Void>> linkToStage(@PathVariable UUID id, @PathVariable UUID stageId) {
        checklistService.linkToStage(id, stageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null, "Checklist linked to stage successfully"));
    }

    @DeleteMapping("/{id}/stages/{stageId}")
    @Operation(summary = "Unlink a checklist from a workflow stage")
    public ResponseEntity<ApiResponse<Void>> unlinkFromStage(@PathVariable UUID id, @PathVariable UUID stageId) {
        checklistService.unlinkFromStage(id, stageId);
        return ResponseEntity.ok(ApiResponse.success(null, "Checklist unlinked from stage successfully"));
    }

    @GetMapping("/workflows/{workFlowId}/links")
    @Operation(summary = "List every stage of a workflow with the checklists linked to each")
    public ResponseEntity<ApiResponse<List<StageChecklistLinksResponse>>> listWorkflowLinks(@PathVariable UUID workFlowId) {
        return ResponseEntity.ok(ApiResponse.success(checklistService.listWorkflowLinks(workFlowId), "success"));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Aggregate checklist coverage stats across all workflows")
    public ResponseEntity<ApiResponse<ChecklistAnalyticsResponse>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(checklistService.getAnalytics(), "success"));
    }
}
