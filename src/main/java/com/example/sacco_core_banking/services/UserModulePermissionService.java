package com.example.sacco_core_banking.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.permission.GrantModulePermissionRequest;
import com.example.sacco_core_banking.dto.permission.UserModulePermissionResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.Permission;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserModulePermission;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.PermissionRepository;
import com.example.sacco_core_banking.repositories.UserModulePermissionRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserModulePermissionService {

    @Autowired
    private UserModulePermissionRepository userModulePermissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModuleRegisterRepository moduleRegisterRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private AuditLogService auditLogService;

    public List<UserModulePermissionResponse> listForUser(UUID userId, UUID requesterSaccoId) {
        User user = findUserInSacco(userId, requesterSaccoId);
        return userModulePermissionRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserModulePermissionResponse grant(User admin, UUID userId, GrantModulePermissionRequest request) {
        User user = findUserInSacco(userId, admin.getSacco().getId());

        ModuleRegister module = moduleRegisterRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        Set<Permission> permissions = new HashSet<>();
        for (String name : request.getPermissionNames()) {
            permissions.add(permissionRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found: " + name)));
        }

        UserModulePermission permission = userModulePermissionRepository
                .findByUserIdAndModuleRegisterId(user.getId(), module.getId())
                .orElseGet(() -> {
                    UserModulePermission created = new UserModulePermission();
                    created.setUser(user);
                    created.setModuleRegister(module);
                    return created;
                });

        permission.setPermissions(permissions);
        permission = userModulePermissionRepository.save(permission);

        auditLogService.record(admin, "MODULE_PERMISSION_GRANTED:" + module.getTextId(), "User", user.getId());

        return toResponse(permission);
    }

    public void revoke(User admin, UUID userId, UUID moduleId) {
        User user = findUserInSacco(userId, admin.getSacco().getId());

        UserModulePermission permission = userModulePermissionRepository
                .findByUserIdAndModuleRegisterId(user.getId(), moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("No permission override found for this module"));

        userModulePermissionRepository.delete(permission);

        auditLogService.record(admin, "MODULE_PERMISSION_REVOKED:" + permission.getModuleRegister().getTextId(),
                "User", user.getId());
    }

    private User findUserInSacco(UUID userId, UUID saccoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getSacco().getId().equals(saccoId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

    private UserModulePermissionResponse toResponse(UserModulePermission permission) {
        return UserModulePermissionResponse.builder()
                .id(permission.getId())
                .moduleId(permission.getModuleRegister().getId())
                .moduleName(permission.getModuleRegister().getName())
                .permissions(permission.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()))
                .build();
    }
}
