package com.fioletowi.farma.exception;

/**
 * Exception thrown when a user tries to perform an action they are not authorized to execute.
 */
public class ForbiddenActionException extends RuntimeException {

    /**
     * Constructs a new ForbiddenActionException with the specified detail message.
     *
     * @param message the detail message explaining why the action is forbidden
     */
    public ForbiddenActionException(String message) {
        super(message);
    }
}
