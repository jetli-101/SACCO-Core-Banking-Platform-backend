package com.example.sacco_core_banking.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.sacco_core_banking.classes.ResourceNotFoundException;
import com.example.sacco_core_banking.dto.member.MemberResponse;
import com.example.sacco_core_banking.dto.member.NextOfKinResponse;
import com.example.sacco_core_banking.dto.member.UpdateMemberRequest;
import com.example.sacco_core_banking.dto.user.UserResponse;
import com.example.sacco_core_banking.entities.Member;
import com.example.sacco_core_banking.entities.NextOfKin;
import com.example.sacco_core_banking.entities.Role;
import com.example.sacco_core_banking.entities.User;
import com.example.sacco_core_banking.entities.UserRole;
import com.example.sacco_core_banking.enums.UserStatus;
import com.example.sacco_core_banking.repositories.MemberRepository;
import com.example.sacco_core_banking.repositories.NextOfKinRepository;
import com.example.sacco_core_banking.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final NextOfKinRepository nextOfKinRepository;
    private final UserRoleRepository userRoleRepository;

    public MemberResponse getMyProfile(User currentUser) {
        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile for this account"));
        return toResponse(member);
    }

    public MemberResponse updateMyProfile(User currentUser, UpdateMemberRequest request) {
        Member member = memberRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No member profile for this account"));

        member.setOccupation(request.getOccupation());
        member.setEmployer(request.getEmployer());
        member.setCounty(request.getCounty());
        member.setConstituency(request.getConstituency());
        member.setWard(request.getWard());
        member.setPostalAddress(request.getPostalAddress());
        member.setPhysicalAddress(request.getPhysicalAddress());
        member = memberRepository.save(member);

        return toResponse(member);
    }

    public MemberResponse getMemberById(UUID memberId, UUID requesterSaccoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!member.getSacco().getId().equals(requesterSaccoId)) {
            throw new ResourceNotFoundException("Member not found");
        }

        return toResponse(member);
    }

    public List<MemberResponse> listMembers(UUID saccoId) {
        return memberRepository.findBySaccoId(saccoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MemberResponse> listPendingMembers(UUID saccoId) {
        return memberRepository.findBySaccoIdAndUserStatus(saccoId, UserStatus.PENDING).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MemberResponse toResponse(Member member) {
        NextOfKin nextOfKin = nextOfKinRepository.findByMemberId(member.getId()).stream()
                .findFirst()
                .orElse(null);

        User user = member.getUser();
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .saccoId(user.getSacco().getId())
                .saccoName(user.getSacco().getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(userRoleRepository.findByUserId(user.getId()).stream()
                        .map(UserRole::getRole)
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .status(user.getStatus().name())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();

        return MemberResponse.builder()
                .id(member.getId())
                .memberNumber(member.getMemberNumber())
                .nationalId(member.getNationalId())
                .firstName(member.getFirstName())
                .middleName(member.getMiddleName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .gender(member.getGender())
                .occupation(member.getOccupation())
                .kraPin(member.getKraPin())
                .employer(member.getEmployer())
                .county(member.getCounty())
                .constituency(member.getConstituency())
                .ward(member.getWard())
                .postalAddress(member.getPostalAddress())
                .physicalAddress(member.getPhysicalAddress())
                .passportPhotoUrl(member.getPassportPhotoUrl())
                .signatureUrl(member.getSignatureUrl())
                .nextOfKin(nextOfKin != null ? NextOfKinResponse.builder()
                        .id(nextOfKin.getId())
                        .name(nextOfKin.getName())
                        .relationship(nextOfKin.getRelationship())
                        .phone(nextOfKin.getPhone())
                        .idNumber(nextOfKin.getIdNumber())
                        .address(nextOfKin.getAddress())
                        .build() : null)
                .user(userResponse)
                .build();
    }
}
