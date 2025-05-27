package com.fioletowi.farma.user;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of {@link UserService} that handles user management operations.
 * Provides methods to create, update, retrieve, archive, and delete users,
 * as well as managing user roles, hiring, and notification preferences.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Mapper<User, UserResponse> userMapper;

    private final PasswordEncoder passwordEncoder;

    private final TeamRepository teamRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final EmailService emailService;

    /**
     * Retrieves a paginated list of users, optionally filtered by role.
     *
     * @param pageable the pagination information
     * @param userRole optional filter by user role
     * @return a page of users matching the criteria
     */
    @Override
    public Page<UserResponse> findAllUsers(Pageable pageable, UserRole userRole) {
        if (userRole != null) {
            return userRepository.findAllByUserRole(pageable, userRole)
                    .map(user -> userMapper.mapTo(user, UserResponse.class));
        } else {
            return userRepository.findAll(pageable)
                    .map(user -> userMapper.mapTo(user, UserResponse.class));
        }
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the user ID
     * @return the user details
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Override
    public UserResponse findUserById(Long id) {
        return userMapper.mapTo(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")), UserResponse.class);
    }

    /**
     * Retrieves the currently authenticated user’s details.
     *
     * @param authentication the authentication context
     * @return the current user details
     * @throws ResourceNotFoundException if the user cannot be found in the database
     */
    @Override
    public UserResponse getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long id = user.getId();
        return userMapper.mapTo(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found")), UserResponse.class);
    }

    /**
     * Creates a new user with default password and settings.
     *
     * @param userRequest the user data to create
     * @return the created user details
     */
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setBirthDate(userRequest.getBirthDate());
        user.setStatus(userRequest.getStatus() != null ? userRequest.getStatus() : UserStatus.ACTIVE);
        user.setAddress(userRequest.getAddress());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode("example123")); // Default password
        user.setCreatedAt(LocalDateTime.now());
        user.setAllowNotifications(userRequest.getAllowNotifications() != null ? userRequest.getAllowNotifications() : true);
        user.setIsArchived(false);
        user.setUserRole(userRequest.getUserRole() != null ? userRequest.getUserRole() : UserRole.WORKER);

        User savedUser = userRepository.save(user);
        return userMapper.mapTo(savedUser, UserResponse.class);
    }

    /**
     * Updates the role of a user. Only allowed if the authenticated user has the required permissions.
     *
     * @param id the user ID to update
     * @param userRole the new user role to assign
     * @param authentication the authentication context of the requesting user
     * @return the updated user details
     * @throws AccessDeniedException if the requesting user lacks permission to assign this role
     * @throws IllegalArgumentException if the user role is invalid or the user is not hired
     * @throws ResourceNotFoundException if the user to update does not exist
     */
    @Override
    public UserResponse updateUserRole(Long id, @Valid UserRole userRole, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getUserRole() == UserRole.MANAGER &&
                (userRole == UserRole.MANAGER || userRole == UserRole.OWNER)) {
            throw new AccessDeniedException("You cannot assign this role to the user.");
        }
        if (!EnumSet.allOf(UserRole.class).contains(userRole)) {
            throw new IllegalArgumentException("Invalid UserRole value");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        if (existingUser.getHiredAt() == null) {
            throw new IllegalArgumentException("User is not hired");
        }
        existingUser.setUserRole(userRole);

        return userMapper.mapTo(userRepository.save(existingUser), UserResponse.class);
    }

    /**
     * Marks a user as hired by setting their hired date and sends a notification email.
     *
     * @param id the user ID to hire
     * @return the updated user details
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserResponse hireUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        existingUser.setHiredAt(LocalDateTime.now());
        User savedUser = userRepository.save(existingUser);

        try {
            emailService.sendHiringEmail(existingUser);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return userMapper.mapTo(savedUser, UserResponse.class);
    }

    /**
     * Allows an admin to partially update a user's details.
     *
     * @param id the user ID to update
     * @param updateUserRequest the partial update data
     * @return the updated user details
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserResponse partialUpdateAdmin(Long id, @Valid UpdateUserRequest updateUserRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        Optional.ofNullable(updateUserRequest.getFirstName()).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(updateUserRequest.getLastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(updateUserRequest.getEmail()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(updateUserRequest.getBirthDate()).ifPresent(existingUser::setBirthDate);
        Optional.ofNullable(updateUserRequest.getStatus()).ifPresent(existingUser::setStatus);
        Optional.ofNullable(updateUserRequest.getNote()).ifPresent(existingUser::setNote);
        Optional.ofNullable(updateUserRequest.getAddress()).ifPresent(existingUser::setAddress);
        Optional.ofNullable(updateUserRequest.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);

        return userMapper.mapTo(userRepository.save(existingUser), UserResponse.class);
    }

    /**
     * Partially updates the authenticated user's details.
     *
     * @param updateUserRequest the partial update data
     * @param authentication the authentication context of the user
     * @return the updated user details
     */
    public UserResponse partialUpdate(@Valid UpdateUserRequest updateUserRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Optional.ofNullable(updateUserRequest.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(updateUserRequest.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(updateUserRequest.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(updateUserRequest.getBirthDate()).ifPresent(user::setBirthDate);
        Optional.ofNullable(updateUserRequest.getAddress()).ifPresent(user::setAddress);
        Optional.ofNullable(updateUserRequest.getPhoneNumber()).ifPresent(user::setPhoneNumber);

        return userMapper.mapTo(userRepository.save(user), UserResponse.class);
    }

    /**
     * Updates settings for the authenticated user, such as notification preferences.
     *
     * @param settingsRequest the settings to update
     * @param authentication the authentication context of the user
     * @return the updated user details
     */
    @Override
    public UserResponse updateUserSettings(UserSettingsRequest settingsRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        user.setAllowNotifications(settingsRequest.isAllowNotifications());
        // Extend here if more settings are added

        return userMapper.mapTo(userRepository.save(user), UserResponse.class);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the user ID to delete
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Archives a user by marking them as archived.
     *
     * @param id the user ID to archive
     * @return the updated user details
     * @throws ResourceNotFoundException if the user does not exist
     */
    @Override
    public UserResponse archiveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        user.setIsArchived(true);
        return userMapper.mapTo(userRepository.save(user), UserResponse.class);
    }

    /**
     * Returns the count of active, hired users.
     *
     * @return a response containing the count of users
     */
    @Override
    public UserCountResponse getNumberOfUsers() {
        return UserCountResponse.builder()
                .count(userRepository.countByIsArchivedFalseAndHiredAtIsNotNull())
                .build();
    }

    /**
     * Finds users who are unavailable (not active and not archived) in teams led by the current manager.
     *
     * @param authentication the authentication context of the manager
     * @return a list of unavailable users
     */
    @Override
    public List<UserResponse> findUnavailableUsers(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        List<Team> teams = teamRepository.findAllByLeaderId(manager.getId());

        Set<User> allMembers = new HashSet<>();
        for (Team team : teams) {
            allMembers.addAll(teamMemberRepository.findUsersByTeamId(team.getId()));
        }

        return allMembers.stream()
                .filter(user -> user.getStatus() != UserStatus.ACTIVE && !user.getIsArchived())
                .map(user -> userMapper.mapTo(user, UserResponse.class))
                .toList();
    }

    /**
     * Finds team members who do not have any tasks assigned, under the current manager.
     *
     * @param authentication the authentication context of the manager
     * @return list of team members without tasks
     */
    public List<UserResponse> findTeamMembersWithoutTasks(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        return userRepository.findTeamMembersWithoutTasksByManagerId(manager.getId())
                .stream().map(user -> userMapper.mapTo(user, UserResponse.class)).toList();
    }
}
