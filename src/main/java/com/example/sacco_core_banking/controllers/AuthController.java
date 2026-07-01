package com.example.sacco_core_banking.controllers;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.auth.AuthResponse;
import com.example.sacco_core_banking.dto.auth.ChangePasswordRequest;
import com.example.sacco_core_banking.dto.auth.LoginRequest;
import com.example.sacco_core_banking.dto.auth.RefreshTokenRequest;
import com.example.sacco_core_banking.dto.auth.RegisterMemberRequest;
import com.example.sacco_core_banking.dto.auth.RegisterResponse;
import com.example.sacco_core_banking.dto.auth.SendOtpRequest;
import com.example.sacco_core_banking.dto.auth.VerifyLoginOtpRequest;
import com.example.sacco_core_banking.dto.auth.VerifyOtpRequest;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.AUTH_PATH)
@Tag(name = "Auth", description = "Member registration, login, and token refresh")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/register")
    @Operation(summary = "Register a member", description = "Creates a PENDING member account awaiting admin approval.")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterMemberRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration received. Your account is pending approval."));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send/resend OTP", description = "Emails a fresh verification code, invalidating any previously issued one.")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null, "Verification code sent"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify registration OTP", description = "Confirms the emailed code and marks the account's email as verified.")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user. For first-login invited staff, returns otpRequired=true and emails an OTP instead of issuing a JWT.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        String message = response.isOtpRequired() ? "OTP sent to your email" : "Login successful";
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    @GetMapping("/activate")
    @Operation(summary = "Activate account", description = "Validates the activation token from the invitation email and activates the account.")
    public ResponseEntity<ApiResponse<Void>> activate(@RequestParam String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok(ApiResponse.success(null, "Account activated successfully. You can now log in."));
    }

    @PostMapping("/verify-login-otp")
    @Operation(summary = "Verify login OTP", description = "Verifies the OTP sent during first-login. Returns JWT; requiresPasswordChange=true if the user must change their temporary password.")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyLoginOtp(@Valid @RequestBody VerifyLoginOtpRequest request) {
        AuthResponse response = authService.verifyLoginOtp(request);
        String message = response.isRequiresPasswordChange() ? "OTP verified. Please set a new password." : "Login successful";
        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

    @PostMapping("/change-temporary-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Change temporary password", description = "Sets a new permanent password for first-login staff. Clears the firstLogin flag.")
    public ResponseEntity<ApiResponse<Void>> changeTemporaryPassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = currentUser.get();
        authService.changeTemporaryPassword(user, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully. Welcome to SmoothSurf Sacco."));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access/refresh token pair.")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }
}
