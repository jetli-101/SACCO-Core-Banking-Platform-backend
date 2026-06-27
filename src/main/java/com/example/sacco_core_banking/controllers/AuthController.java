package com.example.sacco_core_banking.controllers;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.auth.AuthResponse;
import com.example.sacco_core_banking.dto.auth.LoginRequest;
import com.example.sacco_core_banking.dto.auth.RefreshTokenRequest;
import com.example.sacco_core_banking.dto.auth.RegisterMemberRequest;
import com.example.sacco_core_banking.dto.auth.RegisterResponse;
import com.example.sacco_core_banking.dto.auth.SendOtpRequest;
import com.example.sacco_core_banking.dto.auth.VerifyOtpRequest;
import com.example.sacco_core_banking.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.AUTH_PATH)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Member registration, login, and token refresh")
public class AuthController {

    private final AuthService authService;

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
    @Operation(summary = "Verify OTP", description = "Confirms the emailed code and marks the account's email as verified.")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user and issues an access/refresh token pair.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access/refresh token pair.")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
    }
}
