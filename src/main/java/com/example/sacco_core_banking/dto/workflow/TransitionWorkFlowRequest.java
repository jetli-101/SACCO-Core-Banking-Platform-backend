package com.example.sacco_core_banking.dto.workflow;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransitionWorkFlowRequest {

    @NotNull(message = "Transition id is required")
    private UUID transitionId;

    private String comment;

    /** Merged into the instance's data payload as part of taking this transition. */
    private Map<String, Object> dataChanges;

    /** IDs of WorkFlowStageActionChecklist items completed by the caller — required when the matching WorkFlowStageAction has requiresChecklistCompletion. */
    private List<UUID> completedChecklistItemIds;
}
