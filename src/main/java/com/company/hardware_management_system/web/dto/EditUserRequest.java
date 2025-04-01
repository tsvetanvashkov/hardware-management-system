package com.company.hardware_management_system.web.dto;

import com.company.hardware_management_system.department.model.Department;
import com.company.hardware_management_system.office.model.Office;
import com.company.hardware_management_system.user.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {

    @NotBlank(message = "Username can not be empty.")
    @Size(min = 6, max = 20,
            message = "Username must be between {min} and {max} characters")
    private String username;

    @NotBlank(message = "Email can not be empty.")
    @Email
    private String email;

    @NotNull
    private UserRole userRole;

    private boolean isActive;

    @NotNull(message = "Office must be selected")
    private UUID officeId;

    @NotNull(message = "Department must be selected")
    private UUID departmentId;
}
