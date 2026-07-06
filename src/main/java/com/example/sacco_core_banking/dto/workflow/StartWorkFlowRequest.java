package com.example.sacco_core_banking.dto.workflow;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.example.sacco_core_banking.enums.WorkFlowPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * What a calling module sends to kick off a process. processTypeKey resolves the WorkFlow
 * via WorkFlowMapping; data is the module's own payload (claim details, leave dates, ...)
 * and travels with the instance from stage to stage.
 */
@Data
public class StartWorkFlowRequest {

    @NotBlank(message = "Process type key is required")
    private String processTypeKey;

    @NotNull(message = "Reference id is required")
    private UUID referenceId;

    private String processName;

    @NotNull(message = "Priority is required")
    private WorkFlowPriority priority;

    private OffsetDateTime dueDate;

    private UUID assignedTo;

    private Map<String, Object> data;
}
