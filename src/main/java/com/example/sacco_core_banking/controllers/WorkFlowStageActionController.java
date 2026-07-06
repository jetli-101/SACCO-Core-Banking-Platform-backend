package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowStageActionResponse;
import com.example.sacco_core_banking.services.WorkFlowStageActionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Admin CRUD for the optional checklist/approval-comment gate on top of a transition. */
@RestController
@RequestMapping(path = Constants.WORKFLOW_STAGE_ACTIONS_PATH)
@Tag(name = "Workflow Stage Action Management", description = "APIs for managing optional checklist/approval gates on transitions")
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
public class WorkFlowStageActionController {

    @Autowired
    private WorkFlowStageActionService workFlowStageActionService;

    @GetMapping
    @Operation(summary = "List stage actions", description = "Lists stage actions for one stage.")
    public ResponseEntity<ApiResponse<List<WorkFlowStageActionResponse>>> listActions(
            @Parameter(description = "Stage to list actions for", required = true)
            @RequestParam UUID stageId) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStageActionService.listActions(stageId), "success"));
    }

    @PostMapping
    @Operation(summary = "Create a stage action")
    public ResponseEntity<ApiResponse<WorkFlowStageActionResponse>> createAction(@Valid @RequestBody WorkFlowStageActionRequest request) {
        WorkFlowStageActionResponse created = workFlowStageActionService.createAction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Stage action created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a stage action")
    public ResponseEntity<ApiResponse<WorkFlowStageActionResponse>> updateAction(
            @PathVariable UUID id, @Valid @RequestBody WorkFlowStageActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(workFlowStageActionService.updateAction(id, request), "success"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stage action")
    public ResponseEntity<ApiResponse<Void>> deleteAction(@PathVariable UUID id) {
        workFlowStageActionService.deleteAction(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Stage action deleted successfully"));
    }
}
