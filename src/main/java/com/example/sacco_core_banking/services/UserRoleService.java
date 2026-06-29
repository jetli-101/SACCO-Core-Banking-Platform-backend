package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.role.UserRoleRequest;
import com.example.sacco_core_banking.dto.role.UserRoleResponse;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public UserRoleResponse createUserRole(UserRoleRequest request) {
        if (userRoleRepository.findByUserIdAndRoleId(request.getUserId(), request.getRoleId()).isPresent()) {
            throw new DuplicateResourceException("This user already has this role");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return toResponse(userRoleRepository.save(new UserRole(user, role)));
    }

    public UserRoleResponse getUserRoleById(UUID id) {
        return userRoleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));
    }

    public List<UserRoleResponse> findAll() {
        return userRoleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserRoleResponse updateUserRole(UUID id, UserRoleRequest request) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        userRole.setUser(user);
        userRole.setRole(role);

        return toResponse(userRoleRepository.save(userRole));
    }

    public void deleteUserRole(UUID id) {
        UserRole userRole = userRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));
        userRoleRepository.delete(userRole);
    }

    private UserRoleResponse toResponse(UserRole userRole) {
        return UserRoleResponse.builder()
                .id(userRole.getId())
                .userId(userRole.getUser().getId())
                .userEmail(userRole.getUser().getEmail())
                .roleId(userRole.getRole().getId())
                .roleName(userRole.getRole().getName())
                .build();
    }
}
