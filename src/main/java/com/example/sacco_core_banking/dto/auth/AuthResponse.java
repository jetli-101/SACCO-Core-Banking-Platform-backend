package com.example.sacco_core_banking.dto.auth;

import com.example.sacco_core_banking.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    // true when login detected a firstLogin user and sent an OTP instead of issuing JWT.
    // All other fields are null/false in this case.
    private boolean otpRequired;
    private String email;

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserResponse user;

    // true after OTP verification for a firstLogin user — client must redirect to change-password.
    private boolean requiresPasswordChange;
}
