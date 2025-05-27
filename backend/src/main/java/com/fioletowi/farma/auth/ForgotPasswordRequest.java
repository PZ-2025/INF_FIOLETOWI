package com.fioletowi.farma.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for initiating the "forgot password" process.
 * <p>
 * Contains the email of the user who wants to reset their password.
 *
 * @see AuthService#forgotPassword(ForgotPasswordRequest)
 */
@Getter
@Setter
@Builder
public class ForgotPasswordRequest {

    /**
     * The email address of the user requesting password reset.
     * <p>
     * Must be a valid, non-empty email format.
     */
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is not correct")
    private String email;

}
