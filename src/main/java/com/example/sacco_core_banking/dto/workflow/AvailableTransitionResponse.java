package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One transition a process instance could take next, annotated with any gating rules the caller must satisfy before invoking it. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTransitionResponse {
    private UUID transitionId;
    private String name;
    private String actionType;
    private UUID toStageId;
    private String toStageName;
    private boolean requiresChecklistCompletion;
    private boolean requiresApprovalComment;
    private List<WorkFlowStageActionChecklistResponse> checklistItems;
}
