package com.company.hardware_management_system.web.dto;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.user.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddUserRequest {

    @NotBlank(message = "Username can not be empty.")
    @Size(min = 6, max = 20,
            message = "Username must be between {min} and {max} characters")
    private String username;

    @NotBlank(message = "Password can not be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Must contain upper/lower case and number")
    @Size(min = 6, max = 20,
            message = "Password must be between {min} and {max} characters")
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
