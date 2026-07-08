package com.example.sacco_core_banking.dto.workflow;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Aggregate stats backing the Analytics tab of the Workflow Checklist Linking screen. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistAnalyticsResponse {
    private long totalWorkflows;
    private long totalStages;
    private long totalChecklists;
    private long totalChecklistItems;
    private long totalMandatoryItems;
    private List<WorkflowChecklistCoverageResponse> perWorkflow;
}
