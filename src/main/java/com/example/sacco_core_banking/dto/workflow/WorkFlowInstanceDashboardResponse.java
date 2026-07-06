package com.example.sacco_core_banking.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Backs the four stat cards on the WorkFlow Instances screen. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowInstanceDashboardResponse {
    private long activeProcesses;
    private long highPriority;
    private long dueThisWeek;
    private long rejected;
}
