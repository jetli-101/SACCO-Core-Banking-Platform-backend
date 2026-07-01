package com.example.sacco_core_banking.dto.user;

import java.util.Set;

import com.example.sacco_core_banking.validation.KenyanPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Admin-initiated invitation for an internal staff account (teller, accountant, etc.).
 * A secure temporary password is auto-generated and emailed — the admin never sets it.
 * The user must activate via the emailed link, then change the temp password on first login.
 */
@Data
public class InviteUserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @KenyanPhone
    private String phone;

    @NotEmpty(message = "At least one role is required")
    private Set<String> roleNames;
}
