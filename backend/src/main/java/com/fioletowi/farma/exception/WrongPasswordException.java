package com.fioletowi.farma.exception;

/**
 * Exception thrown when an incorrect password is provided during authentication or password validation.
 */
public class WrongPasswordException extends RuntimeException {

    /**
     * Constructs a new WrongPasswordException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public WrongPasswordException(String message) {
        super(message);
    }

}
