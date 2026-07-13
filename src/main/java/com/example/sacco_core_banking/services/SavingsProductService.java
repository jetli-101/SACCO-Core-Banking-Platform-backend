package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.savings.SavingsProductRequest;
import com.example.sacco_core_banking.dto.savings.SavingsProductResponse;
import com.example.sacco_core_banking.entities.SavingsProduct;
import com.example.sacco_core_banking.repositories.SavingsProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SavingsProductService {

    @Autowired
    private SavingsProductRepository savingsProductRepository;

    public List<SavingsProductResponse> listSavingsProducts() {
        return savingsProductRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SavingsProductResponse getSavingsProductById(UUID id) {
        return savingsProductRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Savings product not found"));
    }

    public SavingsProductResponse createSavingsProduct(SavingsProductRequest request) {
        if (savingsProductRepository.findBySavingsProductType(request.getSavingsProductType()).isPresent()) {
            throw new DuplicateResourceException("A product for this savings type already exists");
        }
        SavingsProduct product = new SavingsProduct();
        applyRequest(product, request);
        return toResponse(savingsProductRepository.save(product));
    }

    public SavingsProductResponse updateSavingsProduct(UUID id, SavingsProductRequest request) {
        SavingsProduct product = savingsProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings product not found"));
        applyRequest(product, request);
        return toResponse(savingsProductRepository.save(product));
    }

    public SavingsProductResponse toggleActive(UUID id) {
        SavingsProduct product = savingsProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings product not found"));
        product.setActive(!product.isActive());
        return toResponse(savingsProductRepository.save(product));
    }

    public void deleteSavingsProduct(UUID id) {
        SavingsProduct product = savingsProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Savings product not found"));
        savingsProductRepository.delete(product);
    }

    private void applyRequest(SavingsProduct product, SavingsProductRequest request) {
        product.setSavingsProductType(request.getSavingsProductType());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setInterestRate(request.getInterestRate());
        product.setMinimumOpeningBalance(request.getMinimumOpeningBalance());
        product.setMinimumBalance(request.getMinimumBalance());
        product.setWithdrawable(request.isWithdrawable());
        product.setActive(request.isActive());
    }

    private SavingsProductResponse toResponse(SavingsProduct product) {
        return SavingsProductResponse.builder()
                .id(product.getId())
                .savingsProductType(product.getSavingsProductType())
                .name(product.getName())
                .description(product.getDescription())
                .interestRate(product.getInterestRate())
                .minimumOpeningBalance(product.getMinimumOpeningBalance())
                .minimumBalance(product.getMinimumBalance())
                .withdrawable(product.isWithdrawable())
                .active(product.isActive())
                .build();
    }
}
