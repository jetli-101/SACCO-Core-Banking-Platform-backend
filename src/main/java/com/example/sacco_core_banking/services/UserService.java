package com.example.sacco_core_banking.services;

import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.InvalidStateException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.role.RoleResponse;
import com.example.sacco_core_banking.dto.user.InviteUserRequest;
import com.example.sacco_core_banking.dto.user.UpdateUserRequest;
import com.example.sacco_core_banking.dto.user.UpdateUserStatusRequest;
import com.example.sacco_core_banking.dto.user.UserResponse;
import com.example.sacco_core_banking.entities.ActivationToken;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ActivationTokenService activationTokenService;
    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public List<UserResponse> listUsers(UUID saccoId) {
        return userRepository.findBySaccoId(saccoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID userId, UUID requesterSaccoId) {
        User user = findInSacco(userId, requesterSaccoId);
        return toResponse(user);
    }

    public UserResponse inviteUser(User admin, InviteUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("An account with this phone number already exists");
        }

        String tempPassword = generateTempPassword();

        User user = new User();
        user.setSacco(admin.getSacco());
        user.setUsername(request.getEmail().substring(0, request.getEmail().indexOf('@')));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setStatus(UserStatus.PENDING_ACTIVATION);
        user.setFirstLogin(true);
        user = userRepository.save(user);

        replaceRoles(user, request.getRoleNames());

        ActivationToken activationToken = activationTokenService.createToken(user);
        String activationLink = frontendBaseUrl + "/auth/activate?token=" + activationToken.getToken();

        emailService.sendInvitationEmail(user.getEmail(), request.getFirstName(), tempPassword, activationLink);
        auditLogService.record(admin, "STAFF_USER_INVITED", "User", user.getId());

        return toResponse(user);
    }

    private String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%&*";
        String all = upper + lower + digits + special;

        StringBuilder sb = new StringBuilder(12);
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(special.charAt(random.nextInt(special.length())));
        for (int i = 4; i < 12; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }
        // Fisher-Yates shuffle
        for (int i = sb.length() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(j));
            sb.setCharAt(j, tmp);
        }
        return sb.toString();
    }

    public UserResponse updateUser(User admin, UUID userId, UpdateUserRequest request) {
        User user = findInSacco(userId, admin.getSacco().getId());

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("This username is already taken");
        }

        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user = userRepository.save(user);

        auditLogService.record(admin, "USER_PROFILE_UPDATED", "User", user.getId());

        return toResponse(user);
    }

    public UserResponse updateStatus(User admin, UUID userId, UpdateUserStatusRequest request) {
        User user = findInSacco(userId, admin.getSacco().getId());

        if (user.getId().equals(admin.getId())) {
            throw new InvalidStateException("You cannot change your own account status");
        }

        if (request.getStatus() == UserStatus.PENDING || request.getStatus() == UserStatus.REJECTED) {
            throw new InvalidStateException("Use the member-approval endpoints to approve or reject a pending registration");
        }

        user.setStatus(request.getStatus());
        user = userRepository.save(user);

        auditLogService.record(admin, "USER_STATUS_CHANGED:" + request.getStatus(), "User", user.getId());

        return toResponse(user);
    }

    public List<RoleResponse> getUserRoles(UUID userId, UUID requesterSaccoId) {
        User user = findInSacco(userId, requesterSaccoId);
        return rolesOf(user).stream()
                .map(role -> RoleResponse.builder().id(role.getId()).name(role.getName()).description(role.getDescription()).build())
                .collect(Collectors.toList());
    }

    public UserResponse assignRoles(User admin, UUID userId, Set<String> roleNames) {
        User user = findInSacco(userId, admin.getSacco().getId());

        if (roleNames == null || roleNames.isEmpty()) {
            throw new InvalidStateException("A user must have at least one role");
        }

        replaceRoles(user, roleNames);

        auditLogService.record(admin, "ROLES_ASSIGNED:" + String.join(",", roleNames), "User", user.getId());

        return toResponse(user);
    }

    public UserResponse updateAvatar(User user, MultipartFile file) {
        String previousAvatarUrl = user.getAvatarUrl();

        String url = fileStorageService.store(file, "avatars", user.getId());
        user.setAvatarUrl(url);
        user = userRepository.save(user);

        if (previousAvatarUrl != null) {
            fileStorageService.delete(previousAvatarUrl);
        }

        auditLogService.record(user, "PROFILE_PHOTO_UPDATED", "User", user.getId());

        return toResponse(user);
    }

    private void replaceRoles(User user, Set<String> roleNames) {
        userRoleRepository.deleteByUserId(user.getId());
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            userRoleRepository.save(new UserRole(user, role));
        }
    }

    private List<Role> rolesOf(User user) {
        return userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());
    }

    private User findInSacco(UUID userId, UUID saccoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getSacco().getId().equals(saccoId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return user;
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .saccoId(user.getSacco().getId())
                .saccoName(user.getSacco().getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(rolesOf(user).stream().map(Role::getName).collect(Collectors.toList()))
                .status(user.getStatus().name())
                .avatarUrl(user.getAvatarUrl())
                .firstLogin(user.isFirstLogin())
                .memberId(memberRepository.findByUserId(user.getId()).map(member -> member.getId()).orElse(null))
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
