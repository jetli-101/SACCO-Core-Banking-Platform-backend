package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceHistoryResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowOutTrayItemResponse;
import com.example.sacco_core_banking.services.WorkFlowInstanceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Read-only audit trail — one entry per transition a process instance has taken. */
@RestController
@RequestMapping(path = Constants.WORKFLOW_INSTANCE_HISTORY_PATH)
@Tag(name = "Workflow Instance History", description = "APIs for inspecting a process instance's transition history")
@PreAuthorize("isAuthenticated()")
public class WorkFlowInstanceHistoryController {

    @Autowired
    private WorkFlowInstanceHistoryService workFlowInstanceHistoryService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List an instance's history", description = "Ordered oldest to newest.")
    public ResponseEntity<ApiResponse<List<WorkFlowInstanceHistoryResponse>>> listHistory(
            @Parameter(description = "Process instance to list history for", required = true)
            @RequestParam UUID instanceId) {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceHistoryService.listForInstance(instanceId), "success"));
    }

    @GetMapping("/out-tray")
    @Operation(summary = "List the caller's Out-Tray", description = "Process instances the caller has personally acted on, one row per instance showing their most recent action.")
    public ResponseEntity<ApiResponse<List<WorkFlowOutTrayItemResponse>>> listOutTray() {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceHistoryService.listOutTrayForUser(currentUser.get().getId()), "success"));
    }
}
