package com.example.sacco_core_banking.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.module.ModuleRegisterRequest;
import com.example.sacco_core_banking.dto.module.ModuleResponse;
import com.example.sacco_core_banking.dto.module.ModuleTreeResponse;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.ModuleType;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserGroup;
import com.example.sacco_core_banking.entities.UserGroupMember;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.ModuleTypeRepository;
import com.example.sacco_core_banking.repositories.RolePermissionRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserGroupMemberRepository;
import com.example.sacco_core_banking.repositories.UserGroupRepository;
import com.example.sacco_core_banking.repositories.UserModulePermissionRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ModuleRegisterService {

    @Autowired
    private ModuleRegisterRepository moduleRegisterRepository;
    @Autowired
    private ModuleTypeRepository moduleTypeRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserGroupMemberRepository userGroupMemberRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserModulePermissionRepository userModulePermissionRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    public List<ModuleResponse> listModules() {
        return moduleRegisterRepository.findByParentIsNullOrderByOrderNo().stream()
                .map(this::toResponseTree)
                .collect(Collectors.toList());
    }

    public ModuleResponse getModuleById(UUID id) {
        return moduleRegisterRepository.findById(id)
                .map(this::toResponseTree)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
    }

    public ModuleResponse createModule(ModuleRegisterRequest request) {
        if (moduleRegisterRepository.findByTextId(request.getTextId()).isPresent()) {
            throw new DuplicateResourceException("A module with this text ID already exists");
        }
        if (moduleRegisterRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("A module with this name already exists");
        }

        ModuleRegister module = new ModuleRegister();
        applyRequest(module, request);

        return toResponse(moduleRegisterRepository.save(module));
    }

    public ModuleResponse updateModule(UUID id, ModuleRegisterRequest request) {
        ModuleRegister module = moduleRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        moduleRegisterRepository.findByTextId(request.getTextId())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A module with this text ID already exists");
                });
        moduleRegisterRepository.findByName(request.getName())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateResourceException("A module with this name already exists");
                });

        applyRequest(module, request);

        return toResponse(moduleRegisterRepository.save(module));
    }

    public void deleteModule(UUID id) {
        ModuleRegister module = moduleRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        if (module.getChildren() != null && !module.getChildren().isEmpty()) {
            throw new InvalidStateException("Cannot delete a module that still has child modules — delete or reassign them first");
        }

        // Role.modules owns the FK to this module — detach it from every role that
        // currently grants it before deleting, otherwise the delete violates
        // fk_role_modules_module.
        List<Role> rolesWithModule = roleRepository.findByModuleId(id);
        rolesWithModule.forEach(role -> role.getModules().remove(module));
        roleRepository.saveAll(rolesWithModule);

        // UserGroup.modules is a second, independent grant path to this module
        // (fk_user_group_modules_module) — detach it there too.
        List<UserGroup> groupsWithModule = userGroupRepository.findByModuleId(id);
        groupsWithModule.forEach(group -> group.getModules().remove(module));
        userGroupRepository.saveAll(groupsWithModule);

        // Per-user permission overrides also FK to this module (fk_user_module_permissions_module)
        // — clear those too, otherwise the delete fails for any module an admin has ever
        // granted a user-specific override on.
        userModulePermissionRepository.deleteAll(userModulePermissionRepository.findByModuleRegisterId(id));

        // RolePermission.moduleRegister is a third FK to this module (module-scoped default
        // permissions, as opposed to a role-wide grant) — clear those too.
        rolePermissionRepository.deleteAll(rolePermissionRepository.findByModuleRegisterId(id));

        moduleRegisterRepository.delete(module);
    }

    /** Roles that currently have this module in their menu (inverse side of Role.modules). */
    public List<RoleResponse> getRolesForModule(UUID id) {
        ModuleRegister module = moduleRegisterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        return module.getRoles() == null ? List.of() : module.getRoles().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
    }

    private RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    /**
     * Modules visible to the current user in the sidebar.
     *
     * Opt-in / default-deny: a module is invisible to everyone — including
     * SYSTEM_ADMINISTRATOR — until it has been explicitly granted to at least one of the
     * user's roles or groups via /dashboard/admin/roles. This only gates the sidebar
     * shortcut; actual page/API access is enforced independently by @PreAuthorize on each
     * controller, so nobody is locked out of functionality by an empty menu — an admin can
     * still reach any admin page directly by URL to grant the module in the first place.
     */
    public List<ModuleTreeResponse> getMyMenu(User user) {
        Set<ModuleRegister> grantedViaRoles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .flatMap(role -> role.getModules().stream())
                .collect(Collectors.toSet());

        Set<ModuleRegister> grantedViaGroups = userGroupMemberRepository.findByUserId(user.getId()).stream()
                .map(UserGroupMember::getUserGroup)
                .flatMap(group -> group.getModules().stream())
                .collect(Collectors.toSet());

        Set<ModuleRegister> granted = new HashSet<>(grantedViaRoles);
        granted.addAll(grantedViaGroups);

        Set<ModuleRegister> withParents = new HashSet<>(granted);
        granted.forEach(module -> includeParents(module, withParents));

        Map<ModuleType, List<ModuleRegister>> byType = withParents.stream()
                .filter(module -> module.getModuleType() != null)
                .collect(Collectors.groupingBy(ModuleRegister::getModuleType));

        return byType.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getOrderNo()))
                .map(entry -> ModuleTreeResponse.builder()
                        .moduleTypeId(entry.getKey().getId())
                        .moduleTypeName(entry.getKey().getName())
                        .orderNo(entry.getKey().getOrderNo())
                        .modules(entry.getValue().stream()
                                .filter(module -> module.getParent() == null)
                                .sorted(Comparator.comparingInt(ModuleRegister::getOrderNo))
                                .map(module -> toResponseTreeFiltered(module, withParents))
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private void includeParents(ModuleRegister module, Set<ModuleRegister> accumulator) {
        ModuleRegister parent = module.getParent();
        if (parent != null && !accumulator.contains(parent)) {
            accumulator.add(parent);
            includeParents(parent, accumulator);
        }
    }

    private ModuleResponse toResponseTreeFiltered(ModuleRegister module, Set<ModuleRegister> allowed) {
        List<ModuleResponse> children = module.getChildren() == null ? List.of() : module.getChildren().stream()
                .filter(allowed::contains)
                .sorted(Comparator.comparingInt(ModuleRegister::getOrderNo))
                .map(child -> toResponseTreeFiltered(child, allowed))
                .collect(Collectors.toList());

        return buildResponse(module, children);
    }

    private void applyRequest(ModuleRegister module, ModuleRegisterRequest request) {
        ModuleType moduleType = moduleTypeRepository.findById(request.getModuleTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Module type not found"));

        ModuleRegister parent = null;
        if (request.getParentId() != null) {
            parent = moduleRegisterRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent module not found"));
        }

        module.setName(request.getName());
        module.setTextId(request.getTextId());
        module.setDescription(request.getDescription());
        module.setUrlPath(request.getUrlPath());
        module.setIcon(request.getIcon());
        module.setOrderNo(request.getOrderNo());
        module.setModuleType(moduleType);
        module.setParent(parent);
    }

    private ModuleResponse toResponseTree(ModuleRegister module) {
        List<ModuleResponse> children = module.getChildren() == null ? List.of() : module.getChildren().stream()
                .sorted(Comparator.comparingInt(ModuleRegister::getOrderNo))
                .map(this::toResponseTree)
                .collect(Collectors.toList());

        return buildResponse(module, children);
    }

    private ModuleResponse toResponse(ModuleRegister module) {
        return buildResponse(module, new ArrayList<>());
    }

    private ModuleResponse buildResponse(ModuleRegister module, List<ModuleResponse> children) {
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
                .children(children)
                .build();
    }
}
