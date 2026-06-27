package com.example.sacco_core_banking.dto.permission;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
