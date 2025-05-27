package com.fioletowi.farma.auth;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for user authentication and account-related operations.
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>User registration</li>
 *     <li>User login</li>
 *     <li>Password change</li>
 *     <li>Password reset</li>
 *     <li>Password recovery (forgotten password)</li>
 * </ul>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user in the system and sends an activation or confirmation email.
     *
     * @param request the registration details including email, password, and other required fields
     * @return HTTP 202 Accepted if the registration request was successfully processed
     * @throws MessagingException if sending the confirmation email fails
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        authService.register(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Authenticates a user based on provided credentials.
     *
     * @param request contains the user's email and password
     * @return {@link AuthResponse} containing the JWT token if authentication succeeds
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody @Valid AuthRequest request){
        System.out.println("🔹 Logging in user: " + request.getEmail());

        AuthResponse response = authService.authenticate(request);

        System.out.println("✅ Token returned by AuthService: " + response.getToken());

        return ResponseEntity.ok(response);
    }

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request        contains the current and new password
     * @param authentication the current authentication context (used to identify the user)
     * @return HTTP 200 OK if the password was successfully changed
     */
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication){
        authService.changePassword(request, authentication);
        return ResponseEntity.ok("Password changed");
    }

    /**
     * Initiates the password recovery process for a user who forgot their password.
     * Typically involves sending a reset link to the user's email.
     *
     * @param request contains the email address of the user
     * @return HTTP 202 Accepted if the reset process was initiated successfully
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request){
        authService.forgotPassword(request);
        return ResponseEntity.accepted().build();
    }

    /**
     * Resets the password using a token provided via the password recovery process.
     *
     * @param request contains the reset token and the new password
     * @return HTTP 200 OK if the password was successfully reset
     */
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password changed");
    }

}
