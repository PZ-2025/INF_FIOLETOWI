package com.fioletowi.farma.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the user ID
     * @return the user details as a UserResponse
     */
    UserResponse findUserById(Long id);

    /**
     * Retrieves the currently authenticated user's details.
     *
     * @param authentication the authentication token containing user info
     * @return the current user's details
     */
    UserResponse getCurrentUser(Authentication authentication);

    /**
     * Creates a new user in the system.
     *
     * @param userRequest the request containing user data
     * @return the created user details
     */
    UserResponse createUser(UserRequest userRequest);

    /**
     * Updates the role of a user.
     *
     * @param id the user ID
     * @param userRole the new role to assign
     * @param authentication the authentication token of the requesting user
     * @return the updated user details
     */
    UserResponse updateUserRole(Long id, UserRole userRole, Authentication authentication);

    /**
     * Partially updates a user's details by an admin.
     *
     * @param id the user ID
     * @param updateUserRequest the update data
     * @return the updated user details
     */
    UserResponse partialUpdateAdmin(Long id, UpdateUserRequest updateUserRequest);

    /**
     * Partially updates the authenticated user's details.
     *
     * @param updateUserRequest the update data
     * @param authentication the authentication token of the user
     * @return the updated user details
     */
    UserResponse partialUpdate(UpdateUserRequest updateUserRequest, Authentication authentication);

    /**
     * Updates settings for the authenticated user.
     *
     * @param settingsRequest the new settings
     * @param authentication the authentication token of the user
     * @return the updated user details
     */
    UserResponse updateUserSettings(UserSettingsRequest settingsRequest, Authentication authentication);

    /**
     * Marks a user as hired by setting their hire date.
     *
     * @param id the user ID
     * @return the updated user details
     */
    UserResponse hireUser(Long id);

    /**
     * Deletes a user from the system.
     *
     * @param id the user ID to delete
     */
    void deleteUser(Long id);

    /**
     * Archives a user, marking them as inactive.
     *
     * @param id the user ID to archive
     * @return the updated user details
     */
    UserResponse archiveUser(Long id);

    /**
     * Returns the total number of active, hired users.
     *
     * @return a response containing the count of users
     */
    UserCountResponse getNumberOfUsers();

    /**
     * Finds all users, optionally filtered by role, with pagination.
     *
     * @param pageable pagination information
     * @param userRole optional role filter
     * @return a paginated list of users
     */
    Page<UserResponse> findAllUsers(Pageable pageable, UserRole userRole);

    /**
     * Finds users who are currently unavailable.
     *
     * @param authentication the authentication token of the requesting user
     * @return a list of unavailable users
     */
    List<UserResponse> findUnavailableUsers(Authentication authentication);

    /**
     * Finds team members without assigned tasks under the current manager.
     *
     * @param authentication the authentication token of the manager
     * @return a list of team members without tasks
     */
    List<UserResponse> findTeamMembersWithoutTasks(Authentication authentication);

}
