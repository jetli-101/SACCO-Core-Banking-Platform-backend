package com.example.sacco_core_banking.dto.permission;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModulePermissionResponse {
    private UUID id;
    private UUID moduleId;
    private String moduleName;
    private Set<String> permissions;
}
