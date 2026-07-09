package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.loan.RepaymentRequest;
import com.example.sacco_core_banking.dto.loan.RepaymentResponse;
import com.example.sacco_core_banking.services.RepaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Repayment transactions nested under a loan. */
@RestController
@RequestMapping(path = Constants.LOANS_PATH + "/{loanId}/repayments")
@Tag(name = "Loan Repayments", description = "Repayment transactions recorded against a disbursed loan")
@PreAuthorize("isAuthenticated()")
public class RepaymentController {

    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List repayments for a loan")
    public ResponseEntity<ApiResponse<List<RepaymentResponse>>> listRepayments(@PathVariable UUID loanId) {
        return ResponseEntity.ok(ApiResponse.success(repaymentService.listRepayments(loanId), "success"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Record a repayment against a disbursed loan")
    public ResponseEntity<ApiResponse<RepaymentResponse>> recordRepayment(@PathVariable UUID loanId, @Valid @RequestBody RepaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(repaymentService.recordRepayment(loanId, request, currentUser.get()), "Repayment recorded successfully"));
    }
}
