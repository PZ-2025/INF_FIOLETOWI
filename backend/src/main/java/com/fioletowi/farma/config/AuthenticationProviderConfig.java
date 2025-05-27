package com.fioletowi.farma.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration for the authentication provider.
 * <p>
 * Defines an {@link AuthenticationProvider} bean using {@link DaoAuthenticationProvider}
 * with {@link UserDetailsService} and a {@link PasswordEncoder} bean using BCrypt algorithm.
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationProviderConfig {

    private final UserDetailsService userDetailsService;

    /**
     * Creates and configures the {@link AuthenticationProvider} used for user authentication.
     *
     * @return configured {@link AuthenticationProvider}
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Creates a {@link PasswordEncoder} bean using BCrypt algorithm.
     *
     * @return instance of {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
