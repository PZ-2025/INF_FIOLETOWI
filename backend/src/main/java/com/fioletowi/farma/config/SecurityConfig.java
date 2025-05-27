package com.fioletowi.farma.config;

import com.fioletowi.farma.auth.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class that sets up HTTP security settings.
 * <p>
 * Enables JWT authentication filter, disables CSRF, configures CORS, and defines endpoint access rules.
 * Session management is set to stateless to support JWT.
 * </p>
 * <p>
 * The configuration allows unauthenticated access to "/auth/**" endpoints and restricts
 * access to "/user/me" and "/user/settings" endpoints to users with roles WORKER, MANAGER, or OWNER.
 * All other requests require authentication.
 * </p>
 * <p>
 * Method security is enabled to support role-based access on methods.
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true) // enable role based authentication for methods
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtAuthFilter;

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Enable Cross-Origin Resource Sharing
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection since JWT is used
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll() // Allow unauthenticated access to auth endpoints
                        .requestMatchers("/user/me", "/user/settings")
                        .hasAnyRole("WORKER", "MANAGER", "OWNER") // Restrict access based on roles
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session (no sessions)
                .authenticationProvider(authenticationProvider) // Set the authentication provider
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build();
    }

}
