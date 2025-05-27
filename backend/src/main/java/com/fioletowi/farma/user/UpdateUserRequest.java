package com.fioletowi.farma.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for updating user information.
 * All fields are optional and validated if present.
 */
@Getter
@Setter
@Builder
public class UpdateUserRequest {

    /**
     * User's first name.
     * Must be between 2 and 50 characters if provided.
     */
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Must be between 2 and 50 characters if provided.
     */
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * User's email address.
     * Must be a valid email format if provided.
     */
    @Email(message = "Invalid email format")
    private String email;

    /**
     * User's birth date.
     * Must be a date/time in the past if provided.
     */
    @Past(message = "Birth date must be in the past")
    private LocalDateTime birthDate;

    /**
     * User status (e.g. active, inactive).
     */
    private UserStatus status;

    /**
     * Additional notes about the user.
     * Maximum length 500 characters.
     */
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    /**
     * User's address.
     * Maximum length 255 characters.
     */
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * User's phone number.
     * Must match the specified pattern if provided.
     */
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String phoneNumber;

}
