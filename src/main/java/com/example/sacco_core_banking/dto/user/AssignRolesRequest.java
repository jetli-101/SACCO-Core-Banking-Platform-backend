package com.example.sacco_core_banking.dto.user;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Full replace, not a delta — the user's role set becomes exactly this set. Mirrors
 * eCSRM's assignRolesToUser(Set<UUID>) behaviour.
 */
@Data
public class AssignRolesRequest {

    @NotEmpty(message = "At least one role is required")
    private Set<String> roleNames;
}
