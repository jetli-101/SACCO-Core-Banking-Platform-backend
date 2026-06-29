package com.example.sacco_core_banking.controllers;

import java.util.List;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.member.MemberResponse;
import com.example.sacco_core_banking.dto.member.UpdateMemberRequest;
import com.example.sacco_core_banking.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.MEMBERS_PATH)
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member directory and self-service profile for the logged-in member")
public class MemberController {

    private final MemberService memberService;
    private final CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List members", description = "Lists every SACCO member in the caller's Sacco — the member directory.")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> listMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberService.listMembers(currentUser.get().getSacco().getId()), "Members retrieved"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the SACCO member profile tied to the logged-in account.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMyProfile(currentUser.get()), "Profile retrieved"));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Updates the editable, non-KYC fields on the logged-in member's profile.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyProfile(@Valid @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.updateMyProfile(currentUser.get(), request), "Profile updated"));
    }
}
