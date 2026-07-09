package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.loan.LoanProductRequest;
import com.example.sacco_core_banking.dto.loan.LoanProductResponse;
import com.example.sacco_core_banking.entities.LoanProduct;
import com.example.sacco_core_banking.repositories.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanProductService {

    @Autowired
    private LoanProductRepository loanProductRepository;

    public List<LoanProductResponse> listLoanProducts() {
        return loanProductRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public LoanProductResponse getLoanProductById(UUID id) {
        return loanProductRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
    }

    public LoanProductResponse createLoanProduct(LoanProductRequest request) {
        if (loanProductRepository.findByLoanType(request.getLoanType()).isPresent()) {
            throw new DuplicateResourceException("A product for this loan type already exists");
        }
        LoanProduct product = new LoanProduct();
        applyRequest(product, request);
        return toResponse(loanProductRepository.save(product));
    }

    public LoanProductResponse updateLoanProduct(UUID id, LoanProductRequest request) {
        LoanProduct product = loanProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
        applyRequest(product, request);
        return toResponse(loanProductRepository.save(product));
    }

    public LoanProductResponse toggleActive(UUID id) {
        LoanProduct product = loanProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
        product.setActive(!product.isActive());
        return toResponse(loanProductRepository.save(product));
    }

    public void deleteLoanProduct(UUID id) {
        LoanProduct product = loanProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));
        loanProductRepository.delete(product);
    }

    private void applyRequest(LoanProduct product, LoanProductRequest request) {
        product.setLoanType(request.getLoanType());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setInterestRate(request.getInterestRate());
        product.setMinAmount(request.getMinAmount());
        product.setMaxAmount(request.getMaxAmount());
        product.setMinTermMonths(request.getMinTermMonths());
        product.setMaxTermMonths(request.getMaxTermMonths());
        product.setActive(request.isActive());
    }

    private LoanProductResponse toResponse(LoanProduct product) {
        return LoanProductResponse.builder()
                .id(product.getId())
                .loanType(product.getLoanType())
                .name(product.getName())
                .description(product.getDescription())
                .interestRate(product.getInterestRate())
                .minAmount(product.getMinAmount())
                .maxAmount(product.getMaxAmount())
                .minTermMonths(product.getMinTermMonths())
                .maxTermMonths(product.getMaxTermMonths())
                .active(product.isActive())
                .build();
    }
}
