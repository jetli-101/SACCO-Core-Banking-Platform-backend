package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.loan.GuarantorDecisionRequest;
import com.example.sacco_core_banking.dto.loan.GuarantorRequest;
import com.example.sacco_core_banking.dto.loan.GuarantorResponse;
import com.example.sacco_core_banking.services.GuarantorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Guarantor invitations nested under a loan — the applicant manages the list, the invited
 * member accepts/declines their own row. Ownership is checked in GuarantorService, not here. */
@RestController
@RequestMapping(path = Constants.LOANS_PATH + "/{loanId}/guarantors")
@Tag(name = "Loan Guarantors", description = "Guarantor invitations on a loan application")
@PreAuthorize("isAuthenticated()")
public class GuarantorController {

    @Autowired
    private GuarantorService guarantorService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List guarantors for a loan")
    public ResponseEntity<ApiResponse<List<GuarantorResponse>>> listGuarantors(@PathVariable UUID loanId) {
        return ResponseEntity.ok(ApiResponse.success(guarantorService.listGuarantors(loanId), "success"));
    }

    @PostMapping
    @Operation(summary = "Add a guarantor to the caller's own loan")
    public ResponseEntity<ApiResponse<GuarantorResponse>> addGuarantor(@PathVariable UUID loanId, @Valid @RequestBody GuarantorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(guarantorService.addGuarantor(loanId, request, currentUser.get()), "Guarantor invited successfully"));
    }

    @PutMapping("/{guarantorId}/respond")
    @Operation(summary = "The invited guarantor accepts or declines")
    public ResponseEntity<ApiResponse<GuarantorResponse>> respond(
            @PathVariable UUID loanId, @PathVariable UUID guarantorId, @Valid @RequestBody GuarantorDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(guarantorService.respond(guarantorId, request, currentUser.get()), "Response recorded"));
    }

    @DeleteMapping("/{guarantorId}")
    @Operation(summary = "Remove a guarantor from the caller's own loan")
    public ResponseEntity<ApiResponse<Void>> removeGuarantor(@PathVariable UUID loanId, @PathVariable UUID guarantorId) {
        guarantorService.removeGuarantor(guarantorId, currentUser.get());
        return ResponseEntity.ok(ApiResponse.success(null, "Guarantor removed successfully"));
    }
}
