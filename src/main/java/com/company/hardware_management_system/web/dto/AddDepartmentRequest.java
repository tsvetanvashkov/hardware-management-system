package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddDepartmentRequest {

    @NotBlank(message = "Department name can not be empty.")
    private String name;
}
