package com.fioletowi.farma.user;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO representing the user data returned from the API.
 * Contains detailed information about the user, including personal, contact,
 * status, and role-related fields.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's birth date.
     */
    private LocalDateTime birthDate;

    /**
     * Date and time when the user was hired.
     */
    private LocalDateTime hiredAt;

    /**
     * Date and time when the user was terminated.
     */
    private LocalDateTime terminatedAt;

    /**
     * Current status of the user represented as a String.
     */
    private String status;

    /**
     * Optional note about the user.
     */
    private String note;

    /**
     * Efficiency metric associated with the user.
     */
    private Double efficiency;

    /**
     * Flag indicating whether the user is archived (soft deleted).
     */
    private Boolean isArchived;

    /**
     * Flag indicating if the user allows receiving notifications.
     */
    private Boolean allowNotifications;

    /**
     * Role assigned to the user.
     */
    private UserRole userRole;

    /**
     * User's address.
     */
    private String address;

    /**
     * User's phone number.
     */
    private String phoneNumber;

}
