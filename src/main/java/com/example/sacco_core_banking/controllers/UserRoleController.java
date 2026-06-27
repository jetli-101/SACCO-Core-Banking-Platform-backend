package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.role.UserRoleRequest;
import com.example.sacco_core_banking.dto.role.UserRoleResponse;
import com.example.sacco_core_banking.services.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(path = Constants.USER_ROLES_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "User Role Management", description = "APIs for managing User Roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @Operation(summary = "Create a User Role", description = "Assigns a role group to a user.")
    public ResponseEntity<UserRoleResponse> createUserRole(@Valid @RequestBody UserRoleRequest request) {
        return new ResponseEntity<>(userRoleService.createUserRole(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User Role by ID", description = "Retrieves a User Role by its UUID.")
    public ResponseEntity<UserRoleResponse> getUserRole(
            @Parameter(description = "ID of user role retrieved.", required = true)
            @PathVariable("id") UUID id) {
        return new ResponseEntity<>(userRoleService.getUserRoleById(id), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List All User Roles", description = "Lists all User Roles.")
    public List<UserRoleResponse> listUserRoles() {
        return userRoleService.findAll();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a User Role", description = "Updates an existing User Role based on its UUID.")
    public ResponseEntity<UserRoleResponse> updateUserRole(
            @Parameter(description = "ID of user role being updated.", required = true)
            @PathVariable(value = "id") UUID id,
            @Valid @RequestBody UserRoleRequest request) {
        return new ResponseEntity<>(userRoleService.updateUserRole(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a User Role", description = "Deletes a User Role based on its UUID.")
    public ResponseEntity<Void> deleteUserRole(
            @Parameter(description = "ID of user role being deleted.", required = true)
            @PathVariable UUID id) {
        userRoleService.deleteUserRole(id);
        return ResponseEntity.noContent().build();
    }
}
