package com.fioletowi.farma.exception;

/**
 * Exception thrown when a token has expired and is no longer valid.
 */
public class TokenExpiredException extends RuntimeException {

    /**
     * Constructs a new TokenExpiredException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public TokenExpiredException(String message) {
        super(message);
    }
}
