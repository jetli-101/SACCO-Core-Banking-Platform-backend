package com.example.sacco_core_banking.dto.workflow;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private UUID createdByUserId;
    private String createdByName;
    private int totalStages;
    private UUID initialStageId;
    private String initialStageName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    /** Roles (beyond ROLE_SYSTEM_ADMINISTRATOR) allowed to view/configure this workflow. Empty means admin-only. */
    private List<UUID> allowedRoleIds;
    private List<String> allowedRoleNames;
}
