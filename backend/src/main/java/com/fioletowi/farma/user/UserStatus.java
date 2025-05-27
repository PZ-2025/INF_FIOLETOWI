package com.fioletowi.farma.user;

/**
 * Enum representing the status of a user in the system.
 */
public enum UserStatus {
    /**
     * User is active and has full access.
     */
    ACTIVE,

    /**
     * User is inactive and may not have access.
     */
    INACTIVE,

    /**
     * User is suspended temporarily.
     */
    SUSPENDED,

    /**
     * User is currently on leave.
     */
    ON_LEAVE,

    /**
     * User account is deleted or marked as deleted.
     */
    DELETED
}
