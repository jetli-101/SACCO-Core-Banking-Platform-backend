package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.permission.PermissionRequest;
import com.example.sacco_core_banking.dto.permission.PermissionResponse;
import com.example.sacco_core_banking.entities.Permission;
import com.example.sacco_core_banking.repositories.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PermissionResponse getPermissionById(UUID id) {
        return permissionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("A permission with this name already exists");
        }

        Permission permission = new Permission(request.getName(), request.getDescription());
        return toResponse(permissionRepository.save(permission));
    }

    public PermissionResponse updatePermission(UUID id, PermissionRequest request) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        permission.setName(request.getName());
        permission.setDescription(request.getDescription());

        return toResponse(permissionRepository.save(permission));
    }

    public void deletePermission(UUID id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permissionRepository.delete(permission);
    }

    private PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
