package com.example.sacco_core_banking.dto.module;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModuleTypeRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Text ID is required")
    private String textId;

    private int orderNo;

    private String description;
}
