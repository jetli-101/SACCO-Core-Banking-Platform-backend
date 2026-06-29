package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.permission.RolePermissionRequest;
import com.example.sacco_core_banking.dto.permission.RolePermissionResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.Permission;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.RolePermission;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.PermissionRepository;
import com.example.sacco_core_banking.repositories.RolePermissionRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModuleRegisterRepository moduleRegisterRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public RolePermissionResponse createRolePermission(RolePermissionRequest request) {
        RolePermission rolePermission = new RolePermission();
        applyRequest(rolePermission, request);
        return toResponse(rolePermissionRepository.save(rolePermission));
    }

    public RolePermissionResponse getRolePermissionById(UUID id) {
        return rolePermissionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role permission not found"));
    }

    public List<RolePermissionResponse> findAll() {
        return rolePermissionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RolePermissionResponse updateRolePermission(UUID id, RolePermissionRequest request) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role permission not found"));

        applyRequest(rolePermission, request);

        return toResponse(rolePermissionRepository.save(rolePermission));
    }

    public void deleteRolePermission(UUID id) {
        RolePermission rolePermission = rolePermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role permission not found"));
        rolePermissionRepository.delete(rolePermission);
    }

    private void applyRequest(RolePermission rolePermission, RolePermissionRequest request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        ModuleRegister module = null;
        if (request.getModuleId() != null) {
            module = moduleRegisterRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        }

        rolePermission.setRole(role);
        rolePermission.setModuleRegister(module);
        rolePermission.setPermission(permission);
    }

    private RolePermissionResponse toResponse(RolePermission rolePermission) {
        ModuleRegister module = rolePermission.getModuleRegister();
        return RolePermissionResponse.builder()
                .id(rolePermission.getId())
                .roleId(rolePermission.getRole().getId())
                .roleName(rolePermission.getRole().getName())
                .moduleId(module != null ? module.getId() : null)
                .moduleName(module != null ? module.getName() : null)
                .permissionId(rolePermission.getPermission().getId())
                .permissionName(rolePermission.getPermission().getName())
                .build();
    }
}
