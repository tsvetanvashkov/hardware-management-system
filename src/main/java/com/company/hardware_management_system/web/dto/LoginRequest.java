package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username can not be empty.")
    private String username;

    @NotBlank(message = "Password can not be empty.")
    private String password;
}
