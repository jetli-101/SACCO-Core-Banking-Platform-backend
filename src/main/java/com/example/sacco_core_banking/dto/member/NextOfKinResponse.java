package com.example.sacco_core_banking.dto.member;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NextOfKinResponse {
    private UUID id;
    private String name;
    private String relationship;
    private String phone;
    private String idNumber;
    private String address;
}
