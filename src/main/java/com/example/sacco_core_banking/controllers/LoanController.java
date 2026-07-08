package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.loan.LoanRequest;
import com.example.sacco_core_banking.dto.loan.LoanResponse;
import com.example.sacco_core_banking.services.LoanService;
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

/** Loan applications — the first business module wired to the workflow engine (processTypeKey "LOAN"). */
@RestController
@RequestMapping(path = Constants.LOANS_PATH)
@Tag(name = "Loans", description = "APIs for applying for and reviewing loans")
@PreAuthorize("isAuthenticated()")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a loan", description = "Creates the loan record and starts it in the workflow engine under processTypeKey 'LOAN'.")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(@Valid @RequestBody LoanRequest request) {
        LoanResponse created = loanService.applyForLoan(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Loan application submitted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LOAN_OFFICER','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "List all loans in the caller's Sacco", description = "Staff view — every loan application regardless of who applied.")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> listLoans() {
        return ResponseEntity.ok(ApiResponse.success(loanService.listLoansForSacco(currentUser.get().getSacco().getId()), "success"));
    }

    @GetMapping("/mine")
    @Operation(summary = "List the caller's own loans", description = "Member self-service view.")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> listMyLoans() {
        return ResponseEntity.ok(ApiResponse.success(loanService.listMyLoans(currentUser.get()), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a loan by id")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoanById(id), "success"));
    }
}
