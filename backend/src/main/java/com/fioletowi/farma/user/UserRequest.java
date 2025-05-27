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
 * DTO for creating or updating user data.
 * Contains user personal details, contact information, status, and credentials.
 */
@Getter
@Setter
@Builder
public class UserRequest {

    /**
     * User's first name.
     * Must be between 2 and 50 characters.
     */
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Must be between 2 and 50 characters.
     */
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * User's email address.
     * Must be a valid email format.
     */
    @Email(message = "Invalid email format")
    private String email;

    /**
     * User's birth date.
     * Must be a date in the past.
     */
    @Past(message = "Birth date must be in the past")
    private LocalDateTime birthDate;

    /**
     * Current status of the user.
     */
    private UserStatus status;

    /**
     * Optional note about the user.
     * Max length of 500 characters.
     */
    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    /**
     * User's address.
     * Max length of 255 characters.
     */
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * User's phone number.
     * Must match the specified phone number format pattern.
     */
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
    private String phoneNumber;

    /**
     * Indicates whether the user allows notifications.
     */
    private Boolean allowNotifications;

    /**
     * Role assigned to the user.
     */
    private UserRole userRole;

    /**
     * User's password.
     */
    private String password;

}
