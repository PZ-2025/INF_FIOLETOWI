package com.fioletowi.farma.auth;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    /**
     * User's first name.
     * Must be between 2 and 50 characters and not blank.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Must be between 2 and 50 characters and not blank.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * User's email address.
     * Must be a valid email format, not empty, and not blank.
     */
    @NotEmpty(message = "Email is mandatory")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * User's password.
     * Must be at least 8 characters long, not empty, and not blank.
     */
    @NotEmpty(message = "Password is mandatory")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * User's birth date.
     * Must be a past date and not null.
     */
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDateTime birthDate;

    /**
     * User's address.
     * Must not be blank and max length 255 characters.
     */
    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * User's phone number.
     * Must match phone number format and not be blank.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String phoneNumber;

}
