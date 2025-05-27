package com.fioletowi.farma.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResetPasswordRequest {

    /**
     * Email address of the user requesting password reset.
     * Must be a valid email format, not empty and not blank.
     */
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is not correct")
    private String email;

    /**
     * Password reset token.
     * Must be exactly 10 characters long, not empty and not blank.
     */
    @NotEmpty(message = "Token is mandatory")
    @NotBlank(message = "Token is mandatory")
    @Size(min = 10, max = 10, message = "Token must be exactly 10 characters long")
    private String token;

    /**
     * New password to set for the user.
     * Must be at least 8 characters long, not empty and not blank.
     */
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String newPassword;

}
