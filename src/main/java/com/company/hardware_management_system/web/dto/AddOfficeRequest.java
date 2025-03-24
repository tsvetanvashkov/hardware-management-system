package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddOfficeRequest {

    @NotBlank(message = "Office name can not be empty.")
    private String name;

    @NotBlank(message = "Office location can not be empty.")
    private String location;
}
