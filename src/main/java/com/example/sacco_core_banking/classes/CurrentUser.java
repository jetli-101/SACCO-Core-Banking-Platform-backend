package com.example.sacco_core_banking.classes;

import com.example.sacco_core_banking.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Convenience accessor for "who is making this request" — used by services that need
 * to stamp approvedBy/createdBy or scope a query to the caller's Sacco.
 */
@Component
public class CurrentUser {

    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails)) {
            throw new IllegalStateException("No authenticated user in the current request");
        }
        return ((AppUserDetails) authentication.getPrincipal()).getUser();
    }
}
