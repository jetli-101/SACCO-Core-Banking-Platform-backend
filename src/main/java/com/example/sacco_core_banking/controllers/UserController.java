package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.dto.user.AssignRolesRequest;
import com.example.sacco_core_banking.dto.user.InviteUserRequest;
import com.example.sacco_core_banking.dto.user.UpdateUserRequest;
import com.example.sacco_core_banking.dto.user.UpdateUserStatusRequest;
import com.example.sacco_core_banking.dto.user.UserResponse;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.services.UserService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Users are never hard-deleted (see UserStatus) — "delete" for an account means
 * transitioning it to DEACTIVATED via POST /{id}/status, not removing the row. That
 * keeps the audit trail in AuditLog/MemberApproval meaningful.
 */
@RestController
@RequestMapping(Constants.USERS_PATH)
@PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
@Tag(name = "Users", description = "Staff/member account listing, creation, and status management")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my account", description = "Returns the logged-in user's own account — usable by any role, including staff/admin accounts with no Member profile.")
    public ResponseEntity<ApiResponse<UserResponse>> getMyAccount() {
        User user = currentUser.get();
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(user.getId(), user.getSacco().getId()), "Account retrieved"));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update my account", description = "Updates the logged-in user's own username/phone — usable by any role, including staff/admin accounts with no Member profile.")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyAccount(@Valid @RequestBody UpdateUserRequest request) {
        User user = currentUser.get();
        UserResponse response = userService.updateUser(user, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Account updated"));
    }

    @PostMapping("/me/photo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload my profile photo", description = "Replaces the logged-in user's profile photo — usable by any role.")
    public ResponseEntity<ApiResponse<UserResponse>> uploadMyPhoto(@RequestParam("file") MultipartFile file) {
        UserResponse response = userService.updateAvatar(currentUser.get(), file);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile photo updated"));
    }

    @GetMapping
    @Operation(summary = "List users", description = "Lists every account in the caller's Sacco.")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers() {
        UUID saccoId = currentUser.get().getSacco().getId();
        return ResponseEntity.ok(ApiResponse.success(userService.listUsers(saccoId), "Users retrieved"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Fetches a single account in the caller's Sacco.")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UUID saccoId = currentUser.get().getSacco().getId();
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id, saccoId), "User retrieved"));
    }

    @PostMapping
    @Operation(summary = "Invite a staff account", description = "Sends an invitation email with a temporary password and activation link. Account starts as PENDING_ACTIVATION.")
    public ResponseEntity<ApiResponse<UserResponse>> inviteUser(@Valid @RequestBody InviteUserRequest request) {
        User admin = currentUser.get();
        UserResponse response = userService.inviteUser(admin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Invitation sent successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user's profile", description = "Updates username/phone for an account in the caller's Sacco.")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        User admin = currentUser.get();
        UserResponse response = userService.updateUser(admin, id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User updated"));
    }

    @PostMapping("/{id}/status")
    @Operation(summary = "Change account status", description = "Suspends, locks, deactivates, or reactivates a user.")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        User admin = currentUser.get();
        UserResponse response = userService.updateStatus(admin, id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User status updated"));
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get a user's roles", description = "Lists the role groups a user currently belongs to.")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getUserRoles(@PathVariable UUID id) {
        UUID saccoId = currentUser.get().getSacco().getId();
        return ResponseEntity.ok(ApiResponse.success(userService.getUserRoles(id, saccoId), "User roles retrieved"));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Replace a user's roles", description = "Sets the user's role groups to exactly the given set.")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@PathVariable UUID id,
            @Valid @RequestBody AssignRolesRequest request) {
        User admin = currentUser.get();
        UserResponse response = userService.assignRoles(admin, id, request.getRoleNames());
        return ResponseEntity.ok(ApiResponse.success(response, "Roles updated"));
    }
}
