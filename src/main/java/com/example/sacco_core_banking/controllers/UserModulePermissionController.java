package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.permission.GrantModulePermissionRequest;
import com.example.sacco_core_banking.dto.permission.UserModulePermissionResponse;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.services.UserModulePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.USERS_PATH + "/{userId}/module-permissions")
@Tag(name = "User Module Permissions", description = "Per-user CRUD overrides on top of role-group module access")
public class UserModulePermissionController {

    @Autowired
    private UserModulePermissionService userModulePermissionService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "List a user's module permission overrides")
    public ResponseEntity<ApiResponse<List<UserModulePermissionResponse>>> list(@PathVariable UUID userId) {
        UUID saccoId = currentUser.get().getSacco().getId();
        return ResponseEntity.ok(ApiResponse.success(
                userModulePermissionService.listForUser(userId, saccoId), "Module permissions retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Grant/update a module permission override for a user")
    public ResponseEntity<ApiResponse<UserModulePermissionResponse>> grant(@PathVariable UUID userId,
            @Valid @RequestBody GrantModulePermissionRequest request) {
        User admin = currentUser.get();
        UserModulePermissionResponse response = userModulePermissionService.grant(admin, userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Module permission granted"));
    }

    @DeleteMapping("/{moduleId}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Revoke a module permission override for a user")
    public ResponseEntity<ApiResponse<Void>> revoke(@PathVariable UUID userId, @PathVariable UUID moduleId) {
        User admin = currentUser.get();
        userModulePermissionService.revoke(admin, userId, moduleId);
        return ResponseEntity.ok(ApiResponse.success(null, "Module permission revoked"));
    }
}
