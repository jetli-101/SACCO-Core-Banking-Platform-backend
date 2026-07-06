package com.example.sacco_core_banking.dto.workflow;

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
public class WorkFlowStageActionResponse {
    private UUID id;
    private UUID stageId;
    private UUID transitionId;
    private String transitionName;
    private String name;
    private boolean requiresChecklistCompletion;
    private boolean requiresApprovalComment;
    private boolean active;
    private List<WorkFlowStageActionChecklistResponse> checklistItems;
}
