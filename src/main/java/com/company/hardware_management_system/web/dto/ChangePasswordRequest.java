package com.company.hardware_management_system.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "New password is required.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Must contain upper/lower case and number")
    @Size(min = 6, max = 20,
            message = "Password must be between {min} and {max} characters")
    private String newPassword;

    @NotBlank(message = "Confirmation is required.")
    @Size(min = 6, max = 20,
            message = "Password must be between {min} and {max} characters")
    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

}
