package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.common.AssignMemberRequest;
import com.example.sacco_core_banking.dto.common.GroupMemberResponse;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.usergroup.UserGroupRequest;
import com.example.sacco_core_banking.dto.usergroup.UserGroupResponse;
import com.example.sacco_core_banking.services.UserGroupService;
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

@RestController
@RequestMapping(Constants.USER_GROUPS_PATH)
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "User Groups", description = "Workflow/approval grouping catalogue — a second, independent way (alongside Roles) to grant module access")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping
    @Operation(summary = "List user groups")
    public ResponseEntity<ApiResponse<List<UserGroupResponse>>> listUserGroups() {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.listUserGroups(), "User groups retrieved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user group by ID")
    public ResponseEntity<ApiResponse<UserGroupResponse>> getUserGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.getUserGroupById(id), "User group retrieved"));
    }

    @PostMapping
    @Operation(summary = "Create a user group")
    public ResponseEntity<ApiResponse<UserGroupResponse>> createUserGroup(@Valid @RequestBody UserGroupRequest request) {
        UserGroupResponse response = userGroupService.createUserGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "User group created"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user group")
    public ResponseEntity<ApiResponse<UserGroupResponse>> updateUserGroup(@PathVariable UUID id, @Valid @RequestBody UserGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.updateUserGroup(id, request), "User group updated"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user group", description = "Fails if the group still has any members.")
    public ResponseEntity<ApiResponse<Void>> deleteUserGroup(@PathVariable UUID id) {
        userGroupService.deleteUserGroup(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User group deleted"));
    }

    @GetMapping("/{groupId}/modules")
    @Operation(summary = "Get modules granted to a user group")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByGroupId(@PathVariable UUID groupId) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.getModulesByUserGroupId(groupId), "Modules retrieved"));
    }

    @PostMapping("/{groupId}/modules")
    @Operation(summary = "Grant modules to a user group")
    public ResponseEntity<ApiResponse<UserGroupResponse>> assignModulesToGroup(@PathVariable UUID groupId, @RequestBody Set<UUID> moduleIds) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.assignModulesToGroup(groupId, moduleIds), "Modules assigned"));
    }

    @DeleteMapping("/{groupId}/modules")
    @Operation(summary = "Revoke modules from a user group")
    public ResponseEntity<ApiResponse<UserGroupResponse>> unassignModulesFromGroup(@PathVariable UUID groupId, @RequestBody Set<UUID> moduleIds) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.unassignModulesFromGroup(groupId, moduleIds), "Modules unassigned"));
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "List a user group's members")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> listMembers(@PathVariable UUID groupId) {
        return ResponseEntity.ok(ApiResponse.success(userGroupService.listMembers(groupId), "Members retrieved"));
    }

    @PostMapping("/{groupId}/members")
    @Operation(summary = "Assign a member to a user group")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> addMember(@PathVariable UUID groupId, @Valid @RequestBody AssignMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userGroupService.addMember(groupId, request), "Member assigned"));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    @Operation(summary = "Remove a member from a user group")
    public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable UUID groupId, @PathVariable UUID userId) {
        userGroupService.removeMember(groupId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Member removed"));
    }
}
