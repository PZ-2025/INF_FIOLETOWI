package com.fioletowi.farma.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for changing a user's password.
 * <p>
 * Contains the current password for validation and the new password to be set.
 *
 * @see AuthService#changePassword(ChangePasswordRequest, org.springframework.security.core.Authentication)
 */
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    /**
     * The user's current password.
     * <p>
     * Must be at least 8 characters long and not blank.
     */
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String currentPassword;

    /**
     * The new password the user wants to set.
     * <p>
     * Must be at least 8 characters long and not blank.
     */
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String newPassword;

}
