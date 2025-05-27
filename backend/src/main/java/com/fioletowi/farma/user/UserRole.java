package com.fioletowi.farma.user;

/**
 * Enum representing different roles a user can have in the system.
 */
public enum UserRole {
    /**
     * Regular worker role with standard permissions.
     */
    WORKER,

    /**
     * Manager role with elevated permissions to manage teams and tasks.
     */
    MANAGER,

    /**
     * Owner role with highest permissions, typically for system administrators.
     */
    OWNER
}
