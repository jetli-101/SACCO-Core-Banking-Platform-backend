package com.example.sacco_core_banking.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.AppUserDetails;
import com.example.sacco_core_banking.classes.DuplicateResourceException;
import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.auth.AuthResponse;
import com.example.sacco_core_banking.dto.auth.LoginRequest;
import com.example.sacco_core_banking.dto.auth.RefreshTokenRequest;
import com.example.sacco_core_banking.dto.auth.RegisterMemberRequest;
import com.example.sacco_core_banking.dto.auth.RegisterResponse;
import com.example.sacco_core_banking.dto.user.UserResponse;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.NextOfKin;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.Sacco;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.enums.RoleName;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.NextOfKinRepository;
import com.example.sacco_core_banking.repositories.RoleRepository;
import com.example.sacco_core_banking.repositories.SaccoRepository;
import com.example.sacco_core_banking.repositories.UserRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final SaccoRepository saccoRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MemberRepository memberRepository;
    private final NextOfKinRepository nextOfKinRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public RegisterResponse register(RegisterMemberRequest request) {
        Sacco sacco = saccoRepository.findByRegistrationNumber(request.getSaccoCode())
                .orElseThrow(() -> new ResourceNotFoundException("No Sacco found for code: " + request.getSaccoCode()));

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("An account with this phone number already exists");
        }
        if (memberRepository.existsBySaccoIdAndNationalId(sacco.getId(), request.getNationalId())) {
            throw new DuplicateResourceException("A member with this national ID already exists in this Sacco");
        }

        Role memberRole = roleRepository.findByName(RoleName.ROLE_MEMBER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Default member role has not been seeded"));

        User user = new User();
        user.setSacco(sacco);
        user.setUsername(request.getEmail().substring(0, request.getEmail().indexOf('@')));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);
        user = userRepository.save(user);

        userRoleRepository.save(new UserRole(user, memberRole));

        Member member = new Member();
        member.setSacco(sacco);
        member.setUser(user);
        member.setNationalId(request.getNationalId());
        member.setFirstName(request.getFirstName());
        member.setMiddleName(request.getMiddleName());
        member.setLastName(request.getLastName());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setGender(request.getGender());
        member.setOccupation(request.getOccupation());
        member.setKraPin(request.getKraPin());
        member.setEmployer(request.getEmployer());
        member.setCounty(request.getCounty());
        member.setConstituency(request.getConstituency());
        member.setWard(request.getWard());
        member.setPostalAddress(request.getPostalAddress());
        member.setPhysicalAddress(request.getPhysicalAddress());
        member = memberRepository.save(member);

        NextOfKin nextOfKin = new NextOfKin();
        nextOfKin.setMember(member);
        nextOfKin.setName(request.getNextOfKin().getName());
        nextOfKin.setRelationship(request.getNextOfKin().getRelationship());
        nextOfKin.setPhone(request.getNextOfKin().getPhone());
        nextOfKin.setIdNumber(request.getNextOfKin().getIdNumber());
        nextOfKin.setAddress(request.getNextOfKin().getAddress());
        nextOfKinRepository.save(nextOfKin);

        auditLogService.record(user, "MEMBER_REGISTERED", "Member", member.getId());

        return RegisterResponse.builder()
                .userId(user.getId())
                .memberId(member.getId())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        User user = principal.getUser();

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        auditLogService.record(user, "LOGIN", "User", user.getId());

        return buildAuthResponse(user, principal.getRoles());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtService.isRefreshToken(token)) {
            throw new BadCredentialsException("Token supplied is not a refresh token");
        }

        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!jwtService.isTokenValid(token, new AppUserDetails(user, List.of()))) {
            throw new BadCredentialsException("Refresh token is invalid or expired");
        }

        List<Role> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList());

        return buildAuthResponse(user, roles);
    }

    private AuthResponse buildAuthResponse(User user, List<Role> roles) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .saccoId(user.getSacco().getId())
                .saccoName(user.getSacco().getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roles.stream().map(Role::getName).collect(Collectors.toList()))
                .status(user.getStatus().name())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user, roles))
                .refreshToken(jwtService.generateRefreshToken(user, roles))
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }
}
