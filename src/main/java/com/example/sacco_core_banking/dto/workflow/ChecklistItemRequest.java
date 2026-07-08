package com.example.sacco_core_banking.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChecklistItemRequest {

    @NotBlank(message = "Item text is required")
    private String itemText;

    private boolean required = true;

    private int orderNo;
}
