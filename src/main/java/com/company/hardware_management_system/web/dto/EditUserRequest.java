package com.company.hardware_management_system.web.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class EditUserRequest {

    @URL
    private String profilePicture;

    private String firstName;

    private String lastName;

}
