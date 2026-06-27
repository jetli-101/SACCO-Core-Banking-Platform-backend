package com.example.sacco_core_banking.dto.user;

import java.util.Set;

import com.example.sacco_core_banking.validation.KenyanPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Admin-created staff account (teller, branch manager, accountant, loan officer,
 * auditor, ...) — distinct from AuthController.register, which is the public
 * self-registration path for members only. Staff accounts go straight to ACTIVE
 * since an admin is vouching for them directly; no approval workflow needed.
 */
@Data
public class CreateStaffUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @KenyanPhone
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private Set<String> roleNames;
}
