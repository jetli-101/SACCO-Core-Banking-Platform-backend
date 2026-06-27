package com.example.sacco_core_banking.dto.role;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private UUID roleId;
    private String roleName;
}
