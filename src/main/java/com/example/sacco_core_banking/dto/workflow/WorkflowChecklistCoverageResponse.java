package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row of the Analytics tab's "Checklists per Workflow" breakdown. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowChecklistCoverageResponse {
    private UUID workFlowId;
    private String workFlowName;
    private int checklistCount;
    private int itemCount;
    private int mandatoryCount;
    private int optionalCount;
}
