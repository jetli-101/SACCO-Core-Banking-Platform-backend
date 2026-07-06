package com.example.sacco_core_banking.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Backs the three stat cards on the WorkFlows list screen. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowDashboardResponse {
    private long totalWorkFlows;
    private long activeWorkFlows;
    private long activeProcesses;
    private double averageStages;
}
