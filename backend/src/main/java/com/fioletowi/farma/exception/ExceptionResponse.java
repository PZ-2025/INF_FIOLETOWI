package com.fioletowi.farma.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * Represents the structure of an error response returned by the API.
 * <p>
 * Contains information about the error code, description, a general error message,
 * validation errors as a set of strings, and a map of specific field errors.
 * </p>
 * <p>
 * Fields with empty or null values are excluded from the JSON serialization.
 * </p>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY) // include only non-empty fields
public class ExceptionResponse {

    /**
     * Numeric error code representing the type of error.
     */
    private Integer errorCode;

    /**
     * Detailed description of the error.
     */
    private String errorDescription;

    /**
     * General error message or error identifier.
     */
    private String error;

    /**
     * Set of validation error messages.
     */
    private Set<String> validationErrors;

    /**
     * Map of field names to their respective error messages.
     */
    private Map<String, String> errors;

}
