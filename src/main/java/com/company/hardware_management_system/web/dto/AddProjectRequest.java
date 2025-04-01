package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddProjectRequest {

    @NotBlank(message = "Project name can not be empty.")
    @Size(max = 20, message = "Project name must be less than 20 characters")
    private String name;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;
}
