package com.fioletowi.farma.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user-related operations for authenticated users.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Partially updates user information for the user identified by the given ID.
     *
     * @param id the ID of the user to update
     * @param updateUserRequest the request body containing fields to update
     * @param authentication the authentication token of the currently authenticated user
     * @return the updated user details wrapped in a ResponseEntity
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> partialUpdate(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateUserRequest updateUserRequest,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.partialUpdate(updateUserRequest, authentication));
    }

    /**
     * Retrieves details of the currently authenticated user.
     *
     * @param authentication the authentication token of the currently authenticated user
     * @return the current user's details wrapped in a ResponseEntity
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    /**
     * Updates the settings/preferences of the currently authenticated user.
     *
     * @param settingsRequest the user settings to update
     * @param authentication the authentication token of the currently authenticated user
     * @return the updated user details wrapped in a ResponseEntity
     */
    @PutMapping("/settings")
    public ResponseEntity<UserResponse> updateUserSettings(
            @RequestBody UserSettingsRequest settingsRequest,
            Authentication authentication
    ) {
        UserResponse updatedUser = userService.updateUserSettings(settingsRequest, authentication);
        return ResponseEntity.ok(updatedUser);
    }
}
