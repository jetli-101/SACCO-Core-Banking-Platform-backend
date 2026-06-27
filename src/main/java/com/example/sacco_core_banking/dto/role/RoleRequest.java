package com.example.sacco_core_banking.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
