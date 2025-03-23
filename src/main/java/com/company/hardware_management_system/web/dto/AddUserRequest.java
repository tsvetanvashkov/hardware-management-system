package com.company.hardware_management_system.web.dto;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddUserRequest {

    @NotBlank(message = "Username can not be empty.")
    @Size(min = 6, message = "Username must be at least 6 symbols")
    private String username;

    @NotBlank(message = "Password can not be empty.")
    @Size(min = 6, message = "Password must be at least 6 symbols")
    private String password;

    @NotBlank(message = "Email can not be empty.")
    @Email
    private String email;

    @NotNull
    private UserRole userRole;

    @NotNull
    private Office office;

    @NotNull
    private Department department;

}
