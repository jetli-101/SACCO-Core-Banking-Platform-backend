package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowMappingResponse {
    private UUID id;
    private String processTypeKey;
    private UUID workFlowId;
    private String workFlowName;
    private boolean active;
}
