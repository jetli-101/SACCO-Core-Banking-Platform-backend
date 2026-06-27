package com.example.sacco_core_banking.dto.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectMemberRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    @NotBlank(message = "A reason is required to reject a registration")
    private String reason;
}
