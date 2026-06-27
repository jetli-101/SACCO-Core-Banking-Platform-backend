package com.example.sacco_core_banking.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.role.RoleRequest;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final ModuleRegisterRepository moduleRegisterRepository;
    private final UserRoleRepository userRoleRepository;

    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse getRoleById(UUID id) {
        return roleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("A role with this name already exists");
        }

        Role role = new Role(request.getName(), request.getDescription());
        return toResponse(roleRepository.save(role));
    }

    public RoleResponse updateRole(UUID id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        return toResponse(roleRepository.save(role));
    }

    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (userRoleRepository.existsByRoleId(role.getId())) {
            throw new InvalidStateException("Cannot delete a role that is still assigned to users");
        }

        roleRepository.delete(role);
    }

    public List<ModuleResponse> getModulesByRoleId(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return role.getModules().stream()
                .map(this::toModuleResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse assignModulesToRole(UUID roleId, Set<UUID> moduleIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<ModuleRegister> granted = new HashSet<>(role.getModules());
        for (UUID moduleId : moduleIds) {
            ModuleRegister module = moduleRegisterRepository.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));
            granted.add(module);
        }
        role.setModules(granted);

        return toResponse(roleRepository.save(role));
    }

    public RoleResponse unassignModulesFromRole(UUID roleId, Set<UUID> moduleIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Set<ModuleRegister> remaining = new HashSet<>(role.getModules());
        remaining.removeIf(module -> moduleIds.contains(module.getId()));
        role.setModules(remaining);

        return toResponse(roleRepository.save(role));
    }

    private ModuleResponse toModuleResponse(ModuleRegister module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .textId(module.getTextId())
                .description(module.getDescription())
                .urlPath(module.getUrlPath())
                .icon(module.getIcon())
                .orderNo(module.getOrderNo())
                .moduleTypeId(module.getModuleType() != null ? module.getModuleType().getId() : null)
                .parentId(module.getParent() != null ? module.getParent().getId() : null)
                .children(List.of())
                .build();
    }

    private RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }
}
