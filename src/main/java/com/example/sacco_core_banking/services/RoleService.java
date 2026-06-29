package com.example.sacco_core_banking.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.common.AssignMemberRequest;
import com.example.sacco_core_banking.dto.common.GroupMemberResponse;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.role.RoleRequest;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModuleRegisterRepository moduleRegisterRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userRepository;

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

    public List<GroupMemberResponse> listMembers(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found");
        }

        return userRoleRepository.findByRoleId(roleId).stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    public GroupMemberResponse addMember(UUID roleId, AssignMemberRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userRoleRepository.findByUserIdAndRoleId(user.getId(), roleId).isPresent()) {
            throw new DuplicateResourceException("This user already has this role");
        }

        return toMemberResponse(userRoleRepository.save(new UserRole(user, role)));
    }

    public void removeMember(UUID roleId, UUID userId) {
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ResourceNotFoundException("This user does not have this role"));
        userRoleRepository.delete(userRole);
    }

    private GroupMemberResponse toMemberResponse(UserRole userRole) {
        return GroupMemberResponse.builder()
                .id(userRole.getId())
                .userId(userRole.getUser().getId())
                .username(userRole.getUser().getUsername())
                .email(userRole.getUser().getEmail())
                .build();
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
                .memberCount(userRoleRepository.countByRoleId(role.getId()))
                .build();
    }
}
