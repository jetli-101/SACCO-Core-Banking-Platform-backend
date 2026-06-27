package com.example.sacco_core_banking.dto.module;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModuleRegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Text ID is required")
    private String textId;

    private String description;
    private String urlPath;
    private String icon;
    private int orderNo;

    @NotNull(message = "Module type is required")
    private UUID moduleTypeId;

    private UUID parentId;
}
