package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.workflow.AvailableTransitionResponse;
import com.example.sacco_core_banking.dto.workflow.ReassignWorkFlowInstanceRequest;
import com.example.sacco_core_banking.dto.workflow.StartWorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.TransitionWorkFlowRequest;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceDashboardResponse;
import com.example.sacco_core_banking.dto.workflow.WorkFlowInstanceResponse;
import com.example.sacco_core_banking.services.WorkFlowEngineService;
import com.example.sacco_core_banking.services.WorkFlowInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime side of the engine — powers the Workflow Instances screen (process list,
 * dashboard stat cards) and exposes the two operations any module uses to drive a process:
 * start a new one against its registered WorkFlowMapping, and move an existing one forward.
 */
@RestController
@RequestMapping(path = Constants.WORKFLOW_INSTANCES_PATH)
@Tag(name = "Workflow Instance Management", description = "APIs for running and inspecting workflow process instances")
@PreAuthorize("isAuthenticated()")
public class WorkFlowInstanceController {

    @Autowired
    private WorkFlowInstanceService workFlowInstanceService;
    @Autowired
    private WorkFlowEngineService workFlowEngineService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List process instances")
    public ResponseEntity<ApiResponse<List<WorkFlowInstanceResponse>>> listInstances() {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceService.listInstances(), "success"));
    }

    @GetMapping("/in-tray")
    @Operation(summary = "List the caller's In-Tray", description = "Active instances currently waiting for the caller to act, by direct assignment or stage responsibility.")
    public ResponseEntity<ApiResponse<List<WorkFlowInstanceResponse>>> listInTray() {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceService.listInTrayForUser(currentUser.get()), "success"));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Instance dashboard stats", description = "Active/high-priority/due-this-week/rejected counts.")
    public ResponseEntity<ApiResponse<WorkFlowInstanceDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceService.getDashboard(), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a process instance by id")
    public ResponseEntity<ApiResponse<WorkFlowInstanceResponse>> getInstance(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workFlowInstanceService.getInstanceById(id), "success"));
    }

    @GetMapping("/{id}/available-transitions")
    @Operation(summary = "List available transitions", description = "Transitions the caller could take next from this instance's current stage, with any checklist/comment gates annotated.")
    public ResponseEntity<ApiResponse<List<AvailableTransitionResponse>>> getAvailableTransitions(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(workFlowEngineService.getAvailableTransitions(id), "success"));
    }

    @PostMapping("/start")
    @Operation(summary = "Start a process", description = "The entry point any module calls to attach to the engine via its registered processTypeKey.")
    public ResponseEntity<ApiResponse<WorkFlowInstanceResponse>> startProcess(@Valid @RequestBody StartWorkFlowRequest request) {
        WorkFlowInstanceResponse started = workFlowEngineService.startProcess(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(started, "Process started successfully"));
    }

    @PostMapping("/{id}/transition")
    @Operation(summary = "Transition a process", description = "Moves the instance to the next stage, carrying its data payload with it.")
    public ResponseEntity<ApiResponse<WorkFlowInstanceResponse>> transition(
            @PathVariable UUID id, @Valid @RequestBody TransitionWorkFlowRequest request) {
        WorkFlowInstanceResponse updated = workFlowEngineService.transition(id, request, currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(updated, "Process transitioned successfully"));
    }

    @PostMapping("/{id}/reassign")
    @Operation(summary = "Forward a process to another person", description = "Reassigns the instance to a specific user, same stage — distinct from a transition.")
    public ResponseEntity<ApiResponse<WorkFlowInstanceResponse>> reassign(
            @PathVariable UUID id, @Valid @RequestBody ReassignWorkFlowInstanceRequest request) {
        WorkFlowInstanceResponse updated = workFlowEngineService.reassign(id, request, currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(updated, "Process forwarded successfully"));
    }
}
