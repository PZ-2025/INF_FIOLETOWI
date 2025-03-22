package org.fioletowi.frontend.auth;

import org.fioletowi.frontend.user_profile.UserResponse;
import lombok.Getter;
import lombok.Setter;

public class UserSession {
    @Getter
    @Setter
    private static UserResponse currentUser;

    public static void clear() {
        currentUser = null;
    }
}