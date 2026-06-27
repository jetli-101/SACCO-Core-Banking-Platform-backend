package com.example.sacco_core_banking.dto.permission;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** moduleId is nullable — a null module means the grant applies role-wide, not to one module. */
@Data
public class RolePermissionRequest {

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    private UUID moduleId;

    @NotNull(message = "Permission ID is required")
    private UUID permissionId;
}
