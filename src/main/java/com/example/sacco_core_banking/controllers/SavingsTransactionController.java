package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.savings.SavingsTransactionRequest;
import com.example.sacco_core_banking.dto.savings.SavingsTransactionResponse;
import com.example.sacco_core_banking.services.SavingsTransactionService;
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

/** Deposit/withdrawal transactions nested under a savings account — the Teller's day-to-day
 * counter operations. */
@RestController
@RequestMapping(path = Constants.SAVINGS_PATH + "/{accountId}/transactions")
@Tag(name = "Savings Transactions", description = "Deposit and withdrawal transactions recorded against a savings account")
@PreAuthorize("isAuthenticated()")
public class SavingsTransactionController {

    @Autowired
    private SavingsTransactionService savingsTransactionService;
    @Autowired
    private CurrentUser currentUser;

    @GetMapping
    @Operation(summary = "List transactions for a savings account")
    public ResponseEntity<ApiResponse<List<SavingsTransactionResponse>>> listTransactions(@PathVariable UUID accountId) {
        return ResponseEntity.ok(ApiResponse.success(savingsTransactionService.listTransactions(accountId), "success"));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('TELLER','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Record a deposit against a savings account")
    public ResponseEntity<ApiResponse<SavingsTransactionResponse>> deposit(@PathVariable UUID accountId, @Valid @RequestBody SavingsTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(savingsTransactionService.deposit(accountId, request, currentUser.get()), "Deposit recorded successfully"));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('TELLER','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Record a withdrawal against a savings account")
    public ResponseEntity<ApiResponse<SavingsTransactionResponse>> withdraw(@PathVariable UUID accountId, @Valid @RequestBody SavingsTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(savingsTransactionService.withdraw(accountId, request, currentUser.get()), "Withdrawal recorded successfully"));
    }
}
