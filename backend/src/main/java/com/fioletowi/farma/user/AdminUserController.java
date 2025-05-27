package com.fioletowi.farma.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller providing administrative endpoints for user management.
 * Accessible only to users with roles OWNER or MANAGER depending on the operation.
 */
@RestController
@RequestMapping("/admin/users")
@AllArgsConstructor
public class AdminUserController {

    private UserServiceImpl userService;

    /**
     * Creates a new user.
     * Only accessible by users with role OWNER.
     *
     * @param userRequest the user data to create
     * @return the created user response
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(201).body(createdUser);
    }

    /**
     * Retrieves a pageable list of users, optionally filtered by role.
     * Accessible by OWNER and MANAGER roles.
     *
     * @param pageable paging information
     * @param role optional filter by user role
     * @return a page of user responses
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<UserResponse> getAllUsers(Pageable pageable, @RequestParam(required = false) UserRole role) {
        return userService.findAllUsers(pageable, role);
    }

    /**
     * Retrieves a user by their ID.
     * Accessible by OWNER and MANAGER roles.
     *
     * @param id the user ID
     * @return the user response
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    /**
     * Returns the total number of users.
     * Accessible by OWNER and MANAGER roles.
     *
     * @return the user count response
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserCountResponse> getNumberOfUsers() {
        return ResponseEntity.ok(userService.getNumberOfUsers());
    }

    /**
     * Updates a user's role.
     * Accessible by OWNER and MANAGER roles.
     *
     * @param id the user ID
     * @param userRole the new role to assign
     * @param authentication the authentication context
     * @return the updated user response
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserRole userRole,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.updateUserRole(id, userRole, authentication));
    }

    /**
     * Marks a user as hired.
     * Only accessible by OWNER role.
     *
     * @param id the user ID
     * @return the updated user response
     */
    @PatchMapping("/{id}/hire")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<UserResponse> hireUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.hireUser(id));
    }

    /**
     * Partially updates user data.
     * Only accessible by OWNER role.
     *
     * @param id the user ID
     * @param updateUserRequest fields to update
     * @return the updated user response
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<UserResponse> partialUpdate(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateUserRequest updateUserRequest
    ) {
        return ResponseEntity.ok(userService.partialUpdateAdmin(id, updateUserRequest));
    }

    /**
     * Deletes a user by ID.
     * Only accessible by OWNER role.
     *
     * @param id the user ID
     * @return empty response with 204 No Content status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteUser (@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Archives a user.
     * Only accessible by OWNER role.
     *
     * @param id the user ID
     * @return the updated user response
     */
    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<UserResponse> archiveUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.archiveUser(id));
    }

    /**
     * Finds users currently unavailable (e.g. on leave).
     * Accessible by MANAGER role.
     *
     * @param authentication the authentication context
     * @return list of unavailable users
     */
    @GetMapping("/unavailable")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<List<UserResponse>> findUnavailableUsers(Authentication authentication) {
        return ResponseEntity.ok(userService.findUnavailableUsers(authentication));
    }

    /**
     * Finds team members without assigned tasks managed by the current manager.
     * Accessible by MANAGER role.
     *
     * @param authentication the authentication context
     * @return list of unassigned team members
     */
    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<List<UserResponse>> findTeamMembersWithoutTasksByManager(Authentication authentication) {
        return ResponseEntity.ok(userService.findTeamMembersWithoutTasks(authentication));
    }
}
