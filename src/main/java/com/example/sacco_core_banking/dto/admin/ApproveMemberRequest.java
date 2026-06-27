package com.example.sacco_core_banking.dto.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveMemberRequest {

    @NotNull(message = "Member ID is required")
    private UUID memberId;

    private String comments;
}
