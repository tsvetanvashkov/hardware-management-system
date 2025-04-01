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
public class AddOfficeRequest {

    @NotBlank(message = "Office name can not be empty.")
    @Size(max = 20, message = "Office name must be less than 20 characters")
    private String name;

    @NotBlank(message = "Office location can not be empty.")
    @Size(max = 50, message = "Office location must be less than 50 characters")
    private String location;
}
