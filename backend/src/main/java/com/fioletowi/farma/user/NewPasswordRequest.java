package com.fioletowi.farma.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating or setting a new password.
 * Contains validation to ensure password presence and minimum length.
 */
@Getter
@Setter
@Builder
public class NewPasswordRequest {

    /**
     * The new password.
     * Must not be blank and must be at least 8 characters long.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
