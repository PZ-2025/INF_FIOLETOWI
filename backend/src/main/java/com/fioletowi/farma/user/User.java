package com.fioletowi.farma.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Represents a User entity in the system.
 *
 * Implements {@link UserDetails} for Spring Security authentication and authorization,
 * and {@link Principal} for identifying the user.
 *
 * Contains personal information, authentication details, user role, status, and auditing timestamps.
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's first name.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * User's email address, used as the username for login.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * User's hashed password.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User's date of birth.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    /**
     * Date when the user was hired.
     */
    @Column(name = "hired_at")
    private LocalDateTime hiredAt;

    /**
     * Date when the user's employment was terminated.
     */
    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    /**
     * Current status of the user (e.g., ACTIVE, INACTIVE).
     */
    private UserStatus status;

    /**
     * Additional notes about the user.
     */
    private String note;

    /**
     * User's efficiency score or rating.
     */
    @Column(name = "efficiency")
    private Double efficiency;

    /**
     * Role of the user in the system (e.g., OWNER, MANAGER, EMPLOYEE).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole userRole;

    /**
     * User's address.
     */
    @Column(name = "address", nullable = false)
    private String address;

    /**
     * User's phone number, must be unique.
     */
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    /**
     * Whether the user allows receiving notifications.
     */
    @Column(name = "allow_notifications", nullable = false)
    private Boolean allowNotifications;

    /**
     * Flag indicating if the user is archived (soft deleted).
     */
    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived;

    /**
     * Timestamp when the user was created.
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user was last updated.
     */
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Returns the principal name, which is the user's email.
     *
     * @return the user's email as the principal name
     */
    @Override
    public String getName() {
        return email;
    }

    /**
     * Returns authorities granted to the user.
     *
     * @return a list of granted authorities based on user role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    /**
     * Returns the user's password.
     *
     * @return the hashed password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the user's email as username
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the user's account is non-expired.
     *
     * @return always true (account never expires)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is not locked.
     *
     * @return always true (account never locked)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials are non-expired.
     *
     * @return always true (credentials never expire)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true if user is not archived, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return !isArchived;
    }
}
