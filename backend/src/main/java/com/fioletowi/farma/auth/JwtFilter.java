package com.fioletowi.farma.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * JWT filter that intercepts incoming HTTP requests to validate JWT tokens,
 * authenticate users, and enforce role-based access control.
 * <p>
 * This filter excludes authentication endpoints (e.g. "/auth") from token checks.
 * It extracts the JWT token from the "Authorization" header, validates it,
 * loads user details, and sets the Spring Security context if the token is valid.
 * <p>
 * Additionally, it checks that the user has one of the required roles: ROLE_MANAGER, ROLE_OWNER, or ROLE_WORKER.
 * If the token is expired or the user lacks required roles, it returns appropriate HTTP error responses.
 *
 * @see JwtService for token generation and validation logic
 */
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Filters each HTTP request to:
     * <ul>
     *   <li>Skip JWT checks for "/auth" endpoints.</li>
     *   <li>Extract and validate JWT token from Authorization header.</li>
     *   <li>Authenticate user in Spring Security context if token is valid.</li>
     *   <li>Verify user has required roles and respond with 403 Forbidden if not.</li>
     *   <li>Handle expired tokens by returning 401 Unauthorized with error details.</li>
     * </ul>
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("🔹 JWT Filter executed for request: " + request.getServletPath());

        if (request.getServletPath().contains("/auth")) {
            System.out.println("➡️ Skipping JWT check for auth endpoint");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Brak lub niepoprawny nagłówek Authorization");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("✅ Extracted user email: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                System.out.println("🔹 Loaded user details: " + userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    System.out.println("✅ JWT is valid");

                    // Get user authorities (roles)
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                    System.out.println("🔹 User roles: " + authorities);

                    boolean hasRequiredRole = authorities.stream()
                            .anyMatch(role -> role.getAuthority().equals("ROLE_MANAGER") ||
                                    role.getAuthority().equals("ROLE_OWNER") ||
                                    role.getAuthority().equals("ROLE_WORKER"));

                    if (!hasRequiredRole) {
                        System.out.println("⛔ Access Denied: User lacks required role!");
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");
                        response.getWriter().write("{\"errorCode\": 403, \"errorDescription\": \"Access Denied: Insufficient role permissions\"}");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT Token Expired: " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"errorCode\": 401, \"errorDescription\": \"JWT token has expired\", \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}



// TEST

//protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                @NonNull HttpServletResponse response,
//                                @NonNull FilterChain filterChain) throws ServletException, IOException {
//    System.out.println("🔹 JWT Filter executed for request: " + request.getServletPath());
//
//    // Wypisz wszystkie nagłówki, żeby zobaczyć, co faktycznie przychodzi
//    request.getHeaderNames().asIterator().forEachRemaining(header ->
//            System.out.println("🔍 Header: " + header + " -> " + request.getHeader(header))
//    );
//
//    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//        System.out.println("❌ Brak lub niepoprawny nagłówek Authorization: " + authHeader);
//        filterChain.doFilter(request, response);
//        return;
//    }
//
//    // Jeśli tutaj dotarliśmy, to token jest poprawny
//    System.out.println("✅ Poprawny nagłówek Authorization: " + authHeader);
//
//    final String jwt = authHeader.substring(7);
//    final String userEmail = jwtService.extractUsername(jwt);
//
//    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//        if (jwtService.isTokenValid(jwt, userDetails)) {
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        }
//    }
//    filterChain.doFilter(request, response);
//}


// OLD

//import io.jsonwebtoken.ExpiredJwtException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Service;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//

//@Service
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
//        if (request.getServletPath().contains("/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        final String jwt;
//        final String userEmail;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//
//        try {
//            userEmail = jwtService.extractUsername(jwt);
//
//            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//            filterChain.doFilter(request, response);
//        } catch (ExpiredJwtException e) {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            response.getWriter().write("{\"errorCode\": 401, \"errorDescription\": \"JWT token has expired\", \"error\": \"" + e.getMessage() + "\"}");
//        }
//    }
//
//}