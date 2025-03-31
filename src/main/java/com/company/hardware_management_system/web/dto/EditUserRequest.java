package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUserRequest {

    @Size(max = 20, message = "Firstname length must be less than 20 characters.")
    private String firstName;

    @Size(max = 20, message = "Lastname length must be less than 20 characters.")
    private String lastName;

    @URL
    private String profilePicture;

}
