package com.example.sacco_core_banking.dto.usergroup;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserGroupRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
