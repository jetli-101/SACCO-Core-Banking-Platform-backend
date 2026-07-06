package com.example.sacco_core_banking.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WorkFlowStatusRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Text ID is required")
    private String textId;

    private String description;
}
