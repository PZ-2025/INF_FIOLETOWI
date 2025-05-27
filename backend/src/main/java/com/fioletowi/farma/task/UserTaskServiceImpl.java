package com.fioletowi.farma.task;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of {@link UserTaskService} that handles business logic
 * for managing UserTask entities, including creation, update, retrieval,
 * and deletion of user-task assignments.
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserTaskServiceImpl implements UserTaskService {

    private final UserTaskRepository userTaskRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final Mapper<UserTask, UserTaskResponse> userTaskMapper;
    private final Mapper<Task, TaskResponse> taskMapper;
    private final Mapper<User, UserResponse> userMapper;
    private final EmailService emailService;

    /**
     * Retrieves a paginated list of all user-task assignments.
     *
     * @param pageable pagination and sorting information.
     * @return a page of {@link UserTaskResponse} representing user-task assignments.
     */
    @Override
    public Page<UserTaskResponse> findAllUserTasks(Pageable pageable) {
        return userTaskRepository.findAll(pageable)
                .map(userTask -> {
                    UserTaskResponse response = userTaskMapper.mapTo(userTask, UserTaskResponse.class);
                    response.setTask(taskMapper.mapTo(userTask.getTask(), TaskResponse.class));
                    response.setUser(userMapper.mapTo(userTask.getUser(), UserResponse.class));
                    return response;
                });
    }

    /**
     * Finds a user-task assignment by its ID.
     *
     * @param id the unique identifier of the UserTask.
     * @return the corresponding {@link UserTaskResponse}.
     * @throws ResourceNotFoundException if no UserTask with the given ID exists.
     */
    @Override
    public UserTaskResponse findUserTaskById(Long id) {
        UserTask userTask = userTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserTask with id " + id + " not found"));
        UserTaskResponse response = userTaskMapper.mapTo(userTask, UserTaskResponse.class);
        response.setTask(taskMapper.mapTo(userTask.getTask(), TaskResponse.class));
        response.setUser(userMapper.mapTo(userTask.getUser(), UserResponse.class));
        return response;
    }

    /**
     * Creates a new user-task assignment.
     * Validates that the user exists, is assigned to a team,
     * and is not a team leader before assigning the task.
     * Sends an email notification to the user about the new task.
     *
     * @param newUserTaskRequest data required to create the UserTask.
     * @return the created {@link UserTaskResponse}.
     * @throws ResourceNotFoundException if the referenced user or task does not exist.
     * @throws IllegalStateException if the user is not assigned to a team or is a team leader.
     */
    @Override
    public UserTaskResponse createUserTask(NewUserTaskRequest newUserTaskRequest) {
        UserTask userTask = new UserTask();

        // Fetch the task entity
        Task task = taskRepository.findById(newUserTaskRequest.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + newUserTaskRequest.getTaskId() + " not found"));

        // Fetch the user entity
        User user = userRepository.findById(newUserTaskRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + newUserTaskRequest.getUserId() + " not found"));

        // Validate user team membership
        if (!teamMemberRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("User is not assigned to any team.");
        }

        // Validate user is not a leader of any team
        boolean isLeader = teamMemberRepository.existsByUserIdAndTeam_LeaderId(user.getId(), user.getId());
        if (isLeader) {
            throw new IllegalStateException("Cannot assign a leader to a task.");
        }

        // Assign task and user
        userTask.setTask(task);
        userTask.setUser(user);
        userTask.setCreatedAt(LocalDateTime.now());

        UserTask savedUserTask = userTaskRepository.save(userTask);

        // Send notification email about the new task assignment
        try {
            emailService.sendNewTaskEmail(user, task);
        } catch (MessagingException e) {
            log.error("Failed to send new task email to user {}", user.getId(), e);
        }

        // Map to response DTO
        UserTaskResponse response = userTaskMapper.mapTo(savedUserTask, UserTaskResponse.class);
        response.setTask(taskMapper.mapTo(savedUserTask.getTask(), TaskResponse.class));
        response.setUser(userMapper.mapTo(savedUserTask.getUser(), UserResponse.class));

        return response;
    }

    /**
     * Partially updates an existing user-task assignment.
     * Allows updating the assigned task and/or user.
     *
     * @param id the unique identifier of the UserTask to update.
     * @param updateUserTaskRequest data containing fields to update.
     * @return the updated {@link UserTaskResponse}.
     * @throws ResourceNotFoundException if the UserTask, task, or user does not exist.
     */
    @Override
    public UserTaskResponse partialUpdateUserTask(Long id, UpdateUserTaskRequest updateUserTaskRequest) {
        UserTask userTask = userTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserTask with id " + id + " not found"));

        if (updateUserTaskRequest.getTaskId() != null) {
            Task task = taskRepository.findById(updateUserTaskRequest.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task with id " + updateUserTaskRequest.getTaskId() + " not found"));
            userTask.setTask(task);
        }
        if (updateUserTaskRequest.getUserId() != null) {
            User user = userRepository.findById(updateUserTaskRequest.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + updateUserTaskRequest.getUserId() + " not found"));
            userTask.setUser(user);
        }
        UserTask updatedUserTask = userTaskRepository.save(userTask);
        UserTaskResponse response = userTaskMapper.mapTo(updatedUserTask, UserTaskResponse.class);
        response.setTask(taskMapper.mapTo(updatedUserTask.getTask(), TaskResponse.class));
        response.setUser(userMapper.mapTo(updatedUserTask.getUser(), UserResponse.class));
        return response;
    }

    /**
     * Retrieves all active tasks assigned to a given user by their user ID.
     *
     * @param userId the unique identifier of the user.
     * @return a list of {@link TaskResponse} representing active tasks assigned to the user.
     */
    public List<TaskResponse> getActiveTasksForUserId(Long userId) {
        return userTaskRepository.findActiveTasksByUserId(userId).stream()
                .map(task -> taskMapper.mapTo(task, TaskResponse.class))
                .toList();
    }

    /**
     * Deletes a user-task assignment by its ID.
     *
     * @param id the unique identifier of the UserTask to delete.
     * @throws ResourceNotFoundException if no UserTask with the given ID exists.
     */
    @Override
    public void deleteUserTask(Long id) {
        if (!userTaskRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserTask with id " + id + " not found");
        }
        userTaskRepository.deleteById(id);
    }
}
