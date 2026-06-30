package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.member.ApprovalDecisionRequest;
import com.example.sacco_core_banking.dto.member.MemberResponse;
import com.example.sacco_core_banking.dto.member.UpdateMemberRequest;
import com.example.sacco_core_banking.services.MemberApprovalService;
import com.example.sacco_core_banking.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.MEMBERS_PATH)
@Tag(name = "Members", description = "Member directory and self-service profile for the logged-in member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberApprovalService memberApprovalService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List members", description = "Lists every SACCO member in the caller's Sacco — the member directory.")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> listMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberService.listMembers(currentUser.get().getSacco().getId()), "Members retrieved"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "List pending registrations", description = "Lists members awaiting approval in the caller's Sacco.")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> listPendingMembers() {
        return ResponseEntity.ok(ApiResponse.success(memberApprovalService.listPending(currentUser.get().getSacco().getId()), "Pending members retrieved"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Approve a pending registration", description = "Activates the member's account and assigns a member number.")
    public ResponseEntity<ApiResponse<MemberResponse>> approveMember(@PathVariable UUID id, @RequestBody(required = false) ApprovalDecisionRequest request) {
        String comments = request != null ? request.getComments() : null;
        MemberResponse response = memberApprovalService.approve(currentUser.get(), id, comments);
        return ResponseEntity.ok(ApiResponse.success(response, "Member approved"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Reject a pending registration", description = "Marks the account REJECTED.")
    public ResponseEntity<ApiResponse<MemberResponse>> rejectMember(@PathVariable UUID id, @RequestBody(required = false) ApprovalDecisionRequest request) {
        String comments = request != null ? request.getComments() : null;
        MemberResponse response = memberApprovalService.reject(currentUser.get(), id, comments);
        return ResponseEntity.ok(ApiResponse.success(response, "Member rejected"));
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

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID", description = "Fetches a single member's full record — used by the Member Directory detail view.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getMemberById(id, currentUser.get().getSacco().getId()), "Member retrieved"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Update a member's profile", description = "Admin edit of the same editable, non-KYC fields a member can update on their own profile.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(@PathVariable UUID id, @Valid @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.updateMember(currentUser.get(), id, request), "Member updated"));
    }
}
