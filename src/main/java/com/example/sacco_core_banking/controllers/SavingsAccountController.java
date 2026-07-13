package com.example.sacco_core_banking.controllers;

import java.util.List;
import java.util.UUID;

import com.example.sacco_core_banking.classes.Constants;
import com.example.sacco_core_banking.classes.CurrentUser;
import com.example.sacco_core_banking.dto.ApiResponse;
import com.example.sacco_core_banking.dto.savings.OpenSavingsAccountRequest;
import com.example.sacco_core_banking.dto.savings.SavingsAccountResponse;
import com.example.sacco_core_banking.services.SavingsAccountService;
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

/** Savings accounts — the Sacco's namesake module. Opening an account activates immediately
 * (no workflow engine involvement, unlike Loans); deposits/withdrawals live on
 * SavingsTransactionController, nested under a specific account. */
@RestController
@RequestMapping(path = Constants.SAVINGS_PATH)
@Tag(name = "Savings Accounts", description = "APIs for opening and reviewing member savings accounts")
@PreAuthorize("isAuthenticated()")
public class SavingsAccountController {

    @Autowired
    private SavingsAccountService savingsAccountService;
    @Autowired
    private CurrentUser currentUser;

    @PostMapping("/open")
    @Operation(summary = "Open a savings account", description = "Member self-service — creates an ACTIVE account against the chosen savings product.")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> openAccount(@Valid @RequestBody OpenSavingsAccountRequest request) {
        SavingsAccountResponse created = savingsAccountService.openAccount(request, currentUser.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Savings account opened successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TELLER','LOAN_OFFICER','ACCOUNTANT','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "List all savings accounts in the caller's Sacco", description = "Staff view.")
    public ResponseEntity<ApiResponse<List<SavingsAccountResponse>>> listAccounts() {
        return ResponseEntity.ok(ApiResponse.success(savingsAccountService.listForSacco(currentUser.get().getSacco().getId()), "success"));
    }

    @GetMapping("/mine")
    @Operation(summary = "List the caller's own savings accounts", description = "Member self-service view.")
    public ResponseEntity<ApiResponse<List<SavingsAccountResponse>>> listMyAccounts() {
        return ResponseEntity.ok(ApiResponse.success(savingsAccountService.listMine(currentUser.get()), "success"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a savings account by id")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> getAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(savingsAccountService.getById(id), "success"));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('TELLER','BRANCH_MANAGER','SYSTEM_ADMINISTRATOR')")
    @Operation(summary = "Close a savings account", description = "Only permitted once the account balance is zero.")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> closeAccount(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(savingsAccountService.close(id), "Savings account closed successfully"));
    }
}
