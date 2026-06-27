package com.example.sacco_core_banking.dto.user;

import com.example.sacco_core_banking.validation.KenyanPhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @KenyanPhone
    private String phone;
}
