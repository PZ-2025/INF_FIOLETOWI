package com.fioletowi.farma.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used for user authentication (login).
 * <p>
 * Contains user credentials that must meet validation constraints before processing.
 * Typically used in the {@code /auth/authenticate} endpoint.
 *
 * @see AuthController#authenticate(AuthRequest)
 */
@Getter
@Setter
@Builder
public class AuthRequest {

    /**
     * User's email address.
     * <p>
     * Must not be blank and must follow standard email format.
     */
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email is not correct")
    private String email;

    /**
     * User's password.
     * <p>
     * Must not be blank and must have at least 8 characters.
     */
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;

}
