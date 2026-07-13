package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.savings.SavingsProductRequest;
import com.example.sacco_core_banking.dto.savings.SavingsProductResponse;
import com.example.sacco_core_banking.services.SavingsProductService;
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

/** Savings products — admin-configurable rate/limit metadata per SavingsProductType, including
 * SHARE_CAPITAL. Read is open to any authenticated user (the account-opening picker needs it);
 * mutations are SYSTEM_ADMINISTRATOR/BRANCH_MANAGER only. */
@RestController
@RequestMapping(path = Constants.SAVINGS_PRODUCTS_PATH)
@Tag(name = "Savings Products", description = "Admin-configurable savings product catalog (rate, minimum balances, withdrawability)")
@PreAuthorize("isAuthenticated()")
public class SavingsProductController {

    @Autowired
    private SavingsProductService savingsProductService;

    @GetMapping
    @Operation(summary = "List savings products")
    public ResponseEntity<ApiResponse<List<SavingsProductResponse>>> listSavingsProducts() {
        return ResponseEntity.ok(ApiResponse.success(savingsProductService.listSavingsProducts(), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a savings product by id")
    public ResponseEntity<ApiResponse<SavingsProductResponse>> getSavingsProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(savingsProductService.getSavingsProductById(id), "success"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Create a savings product")
    public ResponseEntity<ApiResponse<SavingsProductResponse>> createSavingsProduct(@Valid @RequestBody SavingsProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savingsProductService.createSavingsProduct(request), "Savings product created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Update a savings product")
    public ResponseEntity<ApiResponse<SavingsProductResponse>> updateSavingsProduct(@PathVariable UUID id, @Valid @RequestBody SavingsProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(savingsProductService.updateSavingsProduct(id, request), "Savings product updated successfully"));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Toggle a savings product's active status")
    public ResponseEntity<ApiResponse<SavingsProductResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(savingsProductService.toggleActive(id), "Savings product status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMINISTRATOR','BRANCH_MANAGER')")
    @Operation(summary = "Delete a savings product")
    public ResponseEntity<ApiResponse<Void>> deleteSavingsProduct(@PathVariable UUID id) {
        savingsProductService.deleteSavingsProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Savings product deleted successfully"));
    }
}
