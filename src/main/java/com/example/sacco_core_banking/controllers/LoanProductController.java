package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.loan.LoanProductRequest;
import com.example.sacco_core_banking.dto.loan.LoanProductResponse;
import com.example.sacco_core_banking.services.LoanProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Loan products — admin-configurable rate/limit metadata per LoanType. Read is open to any
 * authenticated user (the application wizard's product picker needs it); mutations are
 * SYSTEM_ADMINISTRATOR/BRANCH_MANAGER only. */
@RestController
@RequestMapping(path = Constants.LOAN_PRODUCTS_PATH)
@Tag(name = "Loan Products", description = "Admin-configurable loan product catalog (rate, limits, active toggle)")
@PreAuthorize("isAuthenticated()")
public class LoanProductController {

    @Autowired
    private LoanProductService loanProductService;

    @GetMapping
    @Operation(summary = "List loan products")
    public ResponseEntity<ApiResponse<List<LoanProductResponse>>> listLoanProducts() {
        return ResponseEntity.ok(ApiResponse.success(loanProductService.listLoanProducts(), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a loan product by id")
    public ResponseEntity<ApiResponse<LoanProductResponse>> getLoanProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(loanProductService.getLoanProductById(id), "success"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Create a loan product")
    public ResponseEntity<ApiResponse<LoanProductResponse>> createLoanProduct(@Valid @RequestBody LoanProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(loanProductService.createLoanProduct(request), "Loan product created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Update a loan product")
    public ResponseEntity<ApiResponse<LoanProductResponse>> updateLoanProduct(@PathVariable UUID id, @Valid @RequestBody LoanProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(loanProductService.updateLoanProduct(id, request), "Loan product updated successfully"));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Toggle a loan product's active status")
    public ResponseEntity<ApiResponse<LoanProductResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(loanProductService.toggleActive(id), "Loan product status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Delete a loan product")
    public ResponseEntity<ApiResponse<Void>> deleteLoanProduct(@PathVariable UUID id) {
        loanProductService.deleteLoanProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Loan product deleted successfully"));
    }
}
