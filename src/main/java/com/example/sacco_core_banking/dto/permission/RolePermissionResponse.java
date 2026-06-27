package com.example.sacco_core_banking.dto.permission;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    private UUID id;
    private UUID roleId;
    private String roleName;
    private UUID moduleId;
    private String moduleName;
    private UUID permissionId;
    private String permissionName;
}
