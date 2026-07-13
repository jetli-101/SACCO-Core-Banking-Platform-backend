package com.example.sacco_core_banking.services;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.savings.OpenSavingsAccountRequest;
import com.example.sacco_core_banking.dto.savings.SavingsAccountResponse;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.SavingsAccount;
import com.example.sacco_core_banking.entities.SavingsProduct;
import com.example.sacco_core_banking.entities.SavingsTransaction;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.enums.SavingsAccountStatus;
import com.example.sacco_core_banking.enums.SavingsProductType;
import com.example.sacco_core_banking.enums.SavingsTransactionType;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.SavingsAccountRepository;
import com.example.sacco_core_banking.repositories.SavingsProductRepository;
import com.example.sacco_core_banking.repositories.SavingsTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Opening a savings account, unlike a loan, doesn't route through the workflow engine —
 * a deposit account carries no credit risk that needs staged approval, so it activates
 * immediately. A member can only hold one ORDINARY and one SHARE_CAPITAL account (the two
 * "primary" product types in a Kenyan Sacco); FIXED_DEPOSIT and SPECIAL allow multiples.
 */
@Service
@Transactional
public class SavingsAccountService {

    private static final Set<SavingsProductType> SINGLE_ACCOUNT_PRODUCT_TYPES =
            EnumSet.of(SavingsProductType.ORDINARY, SavingsProductType.SHARE_CAPITAL);

    @Autowired
    private SavingsAccountRepository savingsAccountRepository;
    @Autowired
    private SavingsProductRepository savingsProductRepository;
    @Autowired
    private SavingsTransactionRepository savingsTransactionRepository;
    @Autowired
    private MemberRepository memberRepository;

    public SavingsAccountResponse openAccount(OpenSavingsAccountRequest request, User applicant) {
        Member member = memberRepository.findByUserId(applicant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile found for this user"));

        SavingsProduct product = savingsProductRepository.findById(request.getSavingsProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Savings product not found"));
        if (!product.isActive()) {
            throw new InvalidStateException("This savings product is not currently active");
        }

        if (SINGLE_ACCOUNT_PRODUCT_TYPES.contains(product.getSavingsProductType())
                && savingsAccountRepository.existsByMemberIdAndSavingsProductId(member.getId(), product.getId())) {
            throw new DuplicateResourceException("You already have a " + product.getSavingsProductType().name() + " account");
        }

        BigDecimal openingDeposit = request.getOpeningDeposit() != null ? request.getOpeningDeposit() : BigDecimal.ZERO;
        if (product.getMinimumOpeningBalance().compareTo(BigDecimal.ZERO) > 0
                && openingDeposit.compareTo(product.getMinimumOpeningBalance()) < 0) {
            throw new InvalidStateException("Opening deposit must be at least " + product.getMinimumOpeningBalance());
        }

        SavingsAccount account = new SavingsAccount();
        account.setSacco(member.getSacco());
        account.setMember(member);
        account.setSavingsProduct(product);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(SavingsAccountStatus.ACTIVE);
        account.setOpenedAt(OffsetDateTime.now());

        SavingsAccount saved = savingsAccountRepository.save(account);
        saved.setAccountNumber(generateAccountNumber(saved.getId()));
        saved = savingsAccountRepository.save(saved);

        if (openingDeposit.compareTo(BigDecimal.ZERO) > 0) {
            saved.setBalance(openingDeposit);
            saved = savingsAccountRepository.save(saved);

            SavingsTransaction openingTransaction = new SavingsTransaction();
            openingTransaction.setSavingsAccount(saved);
            openingTransaction.setType(SavingsTransactionType.DEPOSIT);
            openingTransaction.setAmount(openingDeposit);
            openingTransaction.setBalanceAfter(openingDeposit);
            openingTransaction.setNarrative("Opening deposit");
            openingTransaction.setTransactedAt(OffsetDateTime.now());
            savingsTransactionRepository.save(openingTransaction);
        }

        return toResponse(saved);
    }

    public List<SavingsAccountResponse> listForSacco(UUID saccoId) {
        return savingsAccountRepository.findBySaccoId(saccoId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<SavingsAccountResponse> listMine(User user) {
        Member member = memberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile found for this user"));
        return savingsAccountRepository.findByMemberId(member.getId()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SavingsAccountResponse getById(UUID id) {
        return toResponse(findAccount(id));
    }

    public SavingsAccountResponse close(UUID id) {
        SavingsAccount account = findAccount(id);
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InvalidStateException("Cannot close an account with a non-zero balance");
        }
        account.setStatus(SavingsAccountStatus.CLOSED);
        account.setClosedAt(OffsetDateTime.now());
        return toResponse(savingsAccountRepository.save(account));
    }

    private SavingsAccount findAccount(UUID id) {
        return savingsAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings account not found"));
    }

    private String generateAccountNumber(UUID accountId) {
        return "SV" + accountId.toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private SavingsAccountResponse toResponse(SavingsAccount account) {
        Member member = account.getMember();
        SavingsProduct product = account.getSavingsProduct();
        return SavingsAccountResponse.builder()
                .id(account.getId())
                .memberId(member.getId())
                .memberName(member.getFirstName() + " " + member.getLastName())
                .memberNumber(member.getMemberNumber())
                .savingsProductId(product.getId())
                .savingsProductName(product.getName())
                .savingsProductType(product.getSavingsProductType())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .openedAt(account.getOpenedAt())
                .closedAt(account.getClosedAt())
                .build();
    }
}
