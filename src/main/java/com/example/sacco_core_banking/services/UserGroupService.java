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
import com.example.sacco_core_banking.dto.usergroup.UserGroupRequest;
import com.example.sacco_core_banking.dto.usergroup.UserGroupResponse;
import com.example.sacco_core_banking.entities.ModuleRegister;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserGroup;
import com.example.sacco_core_banking.entities.UserGroupMember;
import com.example.sacco_core_banking.repositories.ModuleRegisterRepository;
import com.example.sacco_core_banking.repositories.UserGroupMemberRepository;
import com.example.sacco_core_banking.repositories.UserGroupRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private ModuleRegisterRepository moduleRegisterRepository;
    @Autowired
    private UserGroupMemberRepository userGroupMemberRepository;
    @Autowired
    private UserRepository userRepository;

    public List<UserGroupResponse> listUserGroups() {
        return userGroupRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserGroupResponse getUserGroupById(UUID id) {
        return userGroupRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));
    }

    public UserGroupResponse createUserGroup(UserGroupRequest request) {
        if (userGroupRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("A user group with this name already exists");
        }

        UserGroup userGroup = new UserGroup(request.getName(), request.getDescription());
        return toResponse(userGroupRepository.save(userGroup));
    }

    public UserGroupResponse updateUserGroup(UUID id, UserGroupRequest request) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));

        userGroup.setName(request.getName());
        userGroup.setDescription(request.getDescription());

        return toResponse(userGroupRepository.save(userGroup));
    }

    public void deleteUserGroup(UUID id) {
        UserGroup userGroup = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));

        if (userGroupMemberRepository.existsByUserGroupId(userGroup.getId())) {
            throw new InvalidStateException("Cannot delete a user group that is still assigned to users");
        }

        userGroupRepository.delete(userGroup);
    }

    public List<ModuleResponse> getModulesByUserGroupId(UUID userGroupId) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));

        return userGroup.getModules().stream()
                .map(this::toModuleResponse)
                .collect(Collectors.toList());
    }

    public UserGroupResponse assignModulesToGroup(UUID userGroupId, Set<UUID> moduleIds) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));

        Set<ModuleRegister> granted = new HashSet<>(userGroup.getModules());
        for (UUID moduleId : moduleIds) {
            ModuleRegister module = moduleRegisterRepository.findById(moduleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));
            granted.add(module);
        }
        userGroup.setModules(granted);

        return toResponse(userGroupRepository.save(userGroup));
    }

    public UserGroupResponse unassignModulesFromGroup(UUID userGroupId, Set<UUID> moduleIds) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));

        Set<ModuleRegister> remaining = new HashSet<>(userGroup.getModules());
        remaining.removeIf(module -> moduleIds.contains(module.getId()));
        userGroup.setModules(remaining);

        return toResponse(userGroupRepository.save(userGroup));
    }

    public List<GroupMemberResponse> listMembers(UUID userGroupId) {
        if (!userGroupRepository.existsById(userGroupId)) {
            throw new ResourceNotFoundException("User group not found");
        }

        return userGroupMemberRepository.findByUserGroupId(userGroupId).stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    public GroupMemberResponse addMember(UUID userGroupId, AssignMemberRequest request) {
        UserGroup userGroup = userGroupRepository.findById(userGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("User group not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userGroupMemberRepository.findByUserIdAndUserGroupId(user.getId(), userGroupId).isPresent()) {
            throw new DuplicateResourceException("This user is already in this group");
        }

        return toMemberResponse(userGroupMemberRepository.save(new UserGroupMember(user, userGroup)));
    }

    public void removeMember(UUID userGroupId, UUID userId) {
        UserGroupMember member = userGroupMemberRepository.findByUserIdAndUserGroupId(userId, userGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("This user is not in this group"));
        userGroupMemberRepository.delete(member);
    }

    private GroupMemberResponse toMemberResponse(UserGroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .username(member.getUser().getUsername())
                .email(member.getUser().getEmail())
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

    private UserGroupResponse toResponse(UserGroup userGroup) {
        return UserGroupResponse.builder()
                .id(userGroup.getId())
                .name(userGroup.getName())
                .description(userGroup.getDescription())
                .memberCount(userGroupMemberRepository.countByUserGroupId(userGroup.getId()))
                .build();
    }
}
