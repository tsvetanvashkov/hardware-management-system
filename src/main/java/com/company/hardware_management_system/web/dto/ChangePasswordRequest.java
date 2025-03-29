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
public class ChangePasswordRequest {

    @NotBlank(message = "New password is required.")
    @Size(min = 6, message = "Password must be at least 6 symbols")
    private String newPassword;

    @NotBlank(message = "Confirmation is required.")
    @Size(min = 6, message = "Password must be at least 6 symbols")
    private String confirmPassword;

    public boolean isPasswordConfirmed() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }

}
