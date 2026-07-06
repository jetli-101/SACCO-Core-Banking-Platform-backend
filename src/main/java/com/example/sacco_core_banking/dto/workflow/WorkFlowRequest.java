package com.example.sacco_core_banking.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkFlowRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private boolean active = true;
}
