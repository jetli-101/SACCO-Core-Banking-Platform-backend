package com.example.sacco_core_banking.dto.workflow;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkFlowMappingRequest {

    @NotBlank(message = "Process type key is required")
    private String processTypeKey;

    @NotNull(message = "WorkFlow is required")
    private UUID workFlowId;

    private boolean active = true;
}
