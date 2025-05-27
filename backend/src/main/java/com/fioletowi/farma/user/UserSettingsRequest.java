package com.fioletowi.farma.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user settings.
 * Currently, supports notification preferences.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsRequest {
    /**
     * Whether the user allows receiving notifications.
     */
    private boolean allowNotifications;
}
