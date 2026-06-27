package com.example.sacco_core_banking.dto.user;

import com.example.sacco_core_banking.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Used by an admin to suspend/lock/deactivate/reactivate an account. PENDING and
 * REJECTED are excluded here on purpose — those transitions only happen through the
 * member-approval flow (see MemberApprovalService), not this generic status setter.
 */
@Data
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is required")
    private UserStatus status;

    private String reason;
}
