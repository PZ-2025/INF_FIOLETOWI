package com.fioletowi.farma.auth;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.exception.TokenExpiredException;
import com.fioletowi.farma.exception.WrongPasswordException;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Service class responsible for handling authentication, user registration,
 * password changes, and password recovery/reset workflows.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.security.token.expiration}")
    private Long tokenExpiration;

    /**
     * Registers a new user in the system using provided registration data.
     *
     * @param request contains the user's personal and login details
     * @throws MessagingException if email sending fails
     */
    public void register(@Valid RegistrationRequest request) throws MessagingException {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .isArchived(false)
                .allowNotifications(true)
                .userRole(null)
                .build();
        userRepository.save(user);
    }

    /**
     * Authenticates a user using email and password, then generates a JWT token.
     *
     * @param request contains the user's login credentials
     * @return {@link AuthResponse} with the generated JWT token
     */
    public AuthResponse authenticate(@Valid AuthRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullname", user.getEmail());

        String jwtToken = jwtService.generateToken(claims, user);
        return AuthResponse.builder().token(jwtToken).build();
    }

    /**
     * Allows the authenticated user to change their password, validating the current one.
     *
     * @param request        contains the current and new password
     * @param authentication authentication context of the currently logged-in user
     */
    public void changePassword(ChangePasswordRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Initiates the forgot password process by generating a token
     * and sending a reset email to the user.
     *
     * @param request contains the user's email address
     */
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + request.getEmail() + " not found"));

        String token;
        do {
            token = generateToken();
        } while (tokenRepository.findByToken(token) != null);

        tokenRepository.save(
                Token.builder()
                        .email(user.getEmail())
                        .token(token)
                        .expiredAt(LocalDateTime.now().plusMinutes(tokenExpiration))
                        .build()
        );

        try {
            emailService.sendForgotPasswordEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Resets the password for a user using the token received via email.
     *
     * @param request contains the email, reset token, and new password
     */
    public void resetPassword(@Valid ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + request.getEmail() + " not found"));

        Token token = tokenRepository.findByToken(request.getToken());
        if (token == null) {
            throw new ResourceNotFoundException("Invalid or expired token");
        }

        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired at " + token.getExpiredAt());
        }

        if (!token.getEmail().equals(user.getEmail())) {
            throw new ResourceNotFoundException("Invalid token for this user");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(token);
    }

    /**
     * Generates a random numeric token used in the forgot password process.
     *
     * @return a unique 10-digit numeric token as a String
     */
    private String generateToken() {
        int length = 10;
        String characters = "0123456789";
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++) {
            token.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        System.out.println(token);
        return token.toString();
    }
}
