package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.module.ModuleTypeRequest;
import com.example.sacco_core_banking.dto.module.ModuleTypeResponse;
import com.example.sacco_core_banking.entities.ModuleType;
import com.example.sacco_core_banking.repositories.ModuleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModuleTypeService {

    @Autowired
    private ModuleTypeRepository moduleTypeRepository;

    public List<ModuleTypeResponse> listModuleTypes() {
        return moduleTypeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ModuleTypeResponse getModuleTypeById(UUID id) {
        return moduleTypeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Module type not found"));
    }

    public ModuleTypeResponse createModuleType(ModuleTypeRequest request) {
        if (moduleTypeRepository.findByTextId(request.getTextId()).isPresent()) {
            throw new DuplicateResourceException("A module type with this text ID already exists");
        }

        ModuleType moduleType = new ModuleType();
        applyRequest(moduleType, request);

        return toResponse(moduleTypeRepository.save(moduleType));
    }

    public ModuleTypeResponse updateModuleType(UUID id, ModuleTypeRequest request) {
        ModuleType moduleType = moduleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module type not found"));

        applyRequest(moduleType, request);

        return toResponse(moduleTypeRepository.save(moduleType));
    }

    public void deleteModuleType(UUID id) {
        ModuleType moduleType = moduleTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module type not found"));
        moduleTypeRepository.delete(moduleType);
    }

    private void applyRequest(ModuleType moduleType, ModuleTypeRequest request) {
        moduleType.setName(request.getName());
        moduleType.setTextId(request.getTextId());
        moduleType.setOrderNo(request.getOrderNo());
        moduleType.setDescription(request.getDescription());
    }

    private ModuleTypeResponse toResponse(ModuleType moduleType) {
        return ModuleTypeResponse.builder()
                .id(moduleType.getId())
                .name(moduleType.getName())
                .textId(moduleType.getTextId())
                .orderNo(moduleType.getOrderNo())
                .description(moduleType.getDescription())
                .build();
    }
}
