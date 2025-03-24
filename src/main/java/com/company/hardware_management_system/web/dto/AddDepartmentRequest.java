package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddDepartmentRequest {

    @NotBlank(message = "Department name can not be empty.")
    private String name;
}
