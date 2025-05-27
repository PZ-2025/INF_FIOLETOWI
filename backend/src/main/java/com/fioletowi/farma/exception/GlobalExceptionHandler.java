package com.fioletowi.farma.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * Global exception handler for the application.
 * Catches various exceptions thrown during request processing and
 * returns appropriate HTTP responses with standardized error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 404 errors when no handler is found for a request.
     *
     * @param e the NoHandlerFoundException
     * @return ResponseEntity with status 404 and error details
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NoHandlerFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ExceptionResponse.builder()
                        .errorCode(404)
                        .errorDescription("Endpoint not found")
                        .error("Endpoint " + e.getRequestURL() + " does not exists")
                        .build()
        );
    }

    /**
     * Handles database integrity violations (e.g., unique constraint violations).
     *
     * @param e the DataIntegrityViolationException
     * @return ResponseEntity with status 409 and error details
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ExceptionResponse.builder()
                        .errorCode(409)
                        .errorDescription("Violated data integrity")
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles expired JWT token exceptions.
     *
     * @param e the ExpiredJwtException
     * @return ResponseEntity with status 401 and error details
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .errorCode(401)
                        .errorDescription("JWT token has expired")
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles account locked exceptions.
     *
     * @param e the LockedException
     * @return ResponseEntity with status 401 and error details
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .errorCode(302)
                        .errorDescription("Account locked")
                        .build()
        );
    }

    /**
     * Handles disabled account exceptions.
     *
     * @param e the DisabledException
     * @return ResponseEntity with status 401 and error details
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .errorCode(303)
                        .errorDescription("Account disabled")
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles bad credentials exceptions.
     *
     * @param e the BadCredentialsException
     * @return ResponseEntity with status 401 and error details
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ExceptionResponse.builder()
                        .errorCode(303)
                        .errorDescription("Bad credentials")
                        .build()
        );
    }

    /**
     * Handles forbidden action exceptions.
     *
     * @param e the ForbiddenActionException
     * @return ResponseEntity with status 403 and error details
     */
    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ExceptionResponse> handleException(ForbiddenActionException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ExceptionResponse.builder()
                        .errorCode(403)
                        .errorDescription("Forbidden action")
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles messaging exceptions, typically related to email sending.
     *
     * @param e the MessagingException
     * @return ResponseEntity with status 500 and error details
     */
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles mail sending exceptions.
     *
     * @param e the MailSendException
     * @return ResponseEntity with status 500 and error details
     */
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ExceptionResponse> handleException(MailSendException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles validation errors for method arguments.
     *
     * @param e the MethodArgumentNotValidException
     * @return ResponseEntity with status 400 and a set of validation error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException e) {
        Set<String> errors = new HashSet<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            errors.add(err.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .validationErrors(errors)
                        .build()
        );
    }

    /**
     * Handles illegal argument exceptions.
     *
     * @param e the IllegalArgumentException
     * @return ResponseEntity with status 400 and error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        ExceptionResponse.builder()
                                .errorCode(400)
                                .errorDescription("Invalid input data")
                                .error(e.getMessage())
                                .build()
                );
    }

    /**
     * Handles any uncaught exceptions.
     *
     * @param e the Exception
     * @return ResponseEntity with status 500 and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .errorDescription("Internal error")
                        .error(e.getMessage())
                        .build()
        );
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param e the ResourceNotFoundException
     * @return ResponseEntity with status 404 and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ExceptionResponse.builder()
                        .errorCode(404)
                        .errorDescription(e.getMessage())
                        .error(e.getMessage())
                        .build()
        );
    }

}
