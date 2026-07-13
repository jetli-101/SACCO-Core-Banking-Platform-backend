package com.example.sacco_core_banking.services;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.savings.SavingsTransactionRequest;
import com.example.sacco_core_banking.dto.savings.SavingsTransactionResponse;
import com.example.sacco_core_banking.entities.SavingsAccount;
import com.example.sacco_core_banking.entities.SavingsTransaction;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.SavingsAccountStatus;
import com.example.sacco_core_banking.enums.SavingsTransactionType;
import com.example.sacco_core_banking.repositories.SavingsAccountRepository;
import com.example.sacco_core_banking.repositories.SavingsTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Deposit/withdrawal transactions recorded by staff (typically a Teller) against a savings
 * account. The account's balance column is the running total; each transaction snapshots
 * balanceAfter so a statement stays correct even once later transactions move the balance on. */
@Service
@Transactional
public class SavingsTransactionService {

    @Autowired
    private SavingsTransactionRepository savingsTransactionRepository;
    @Autowired
    private SavingsAccountRepository savingsAccountRepository;

    public List<SavingsTransactionResponse> listTransactions(UUID accountId) {
        findAccount(accountId);
        return savingsTransactionRepository.findBySavingsAccountIdOrderByTransactedAtDesc(accountId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public SavingsTransactionResponse deposit(UUID accountId, SavingsTransactionRequest request, User recordedBy) {
        SavingsAccount account = findAccount(accountId);
        if (account.getStatus() == SavingsAccountStatus.CLOSED) {
            throw new InvalidStateException("Cannot deposit into a closed account");
        }

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        if (account.getStatus() == SavingsAccountStatus.DORMANT) {
            account.setStatus(SavingsAccountStatus.ACTIVE);
        }
        savingsAccountRepository.save(account);

        SavingsTransaction transaction = buildTransaction(account, SavingsTransactionType.DEPOSIT, request, newBalance, recordedBy);
        return toResponse(savingsTransactionRepository.save(transaction));
    }

    public SavingsTransactionResponse withdraw(UUID accountId, SavingsTransactionRequest request, User recordedBy) {
        SavingsAccount account = findAccount(accountId);
        if (account.getStatus() != SavingsAccountStatus.ACTIVE) {
            throw new InvalidStateException("Withdrawals require an active account");
        }
        if (!account.getSavingsProduct().isWithdrawable()) {
            throw new InvalidStateException("This savings product does not allow withdrawals");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        if (newBalance.compareTo(account.getSavingsProduct().getMinimumBalance()) < 0) {
            throw new InvalidStateException("Insufficient funds — balance cannot go below the product's minimum balance");
        }

        account.setBalance(newBalance);
        savingsAccountRepository.save(account);

        SavingsTransaction transaction = buildTransaction(account, SavingsTransactionType.WITHDRAWAL, request, newBalance, recordedBy);
        return toResponse(savingsTransactionRepository.save(transaction));
    }

    private SavingsTransaction buildTransaction(SavingsAccount account, SavingsTransactionType type, SavingsTransactionRequest request, BigDecimal balanceAfter, User recordedBy) {
        SavingsTransaction transaction = new SavingsTransaction();
        transaction.setSavingsAccount(account);
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(balanceAfter);
        transaction.setMethod(request.getMethod());
        transaction.setNarrative(request.getNarrative());
        transaction.setTransactedAt(request.getTransactedAt() != null ? request.getTransactedAt() : OffsetDateTime.now());
        transaction.setRecordedBy(recordedBy);
        return transaction;
    }

    private SavingsAccount findAccount(UUID accountId) {
        return savingsAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings account not found"));
    }

    private SavingsTransactionResponse toResponse(SavingsTransaction transaction) {
        return SavingsTransactionResponse.builder()
                .id(transaction.getId())
                .savingsAccountId(transaction.getSavingsAccount().getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .method(transaction.getMethod())
                .narrative(transaction.getNarrative())
                .transactedAt(transaction.getTransactedAt())
                .recordedByName(transaction.getRecordedBy() != null ? transaction.getRecordedBy().getUsername() : null)
                .build();
    }
}
