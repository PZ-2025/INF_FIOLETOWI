package com.fioletowi.farma.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object returned after successful user authentication.
 * <p>
 * Contains the JWT token used for authorizing subsequent requests.
 *
 * @see AuthController#authenticate(AuthRequest)
 */
@Getter
@Setter
@Builder
public class AuthResponse {

    /**
     * JWT token representing the authenticated session.
     */
    private String token;

}
