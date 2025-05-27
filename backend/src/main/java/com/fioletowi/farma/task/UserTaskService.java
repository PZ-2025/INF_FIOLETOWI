package com.fioletowi.farma.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface defining operations related to UserTask entities.
 */
public interface UserTaskService {

    /**
     * Retrieves a paginated list of all user-task associations.
     *
     * @param pageable Pagination information including page number and size.
     * @return A page of UserTaskResponse objects.
     */
    Page<UserTaskResponse> findAllUserTasks(Pageable pageable);

    /**
     * Finds a specific user-task association by its unique ID.
     *
     * @param id The unique identifier of the UserTask.
     * @return The UserTaskResponse object matching the given ID.
     */
    UserTaskResponse findUserTaskById(Long id);

    /**
     * Creates a new user-task association.
     *
     * @param newUserTaskRequest Request object containing data to create the UserTask.
     * @return The created UserTaskResponse object.
     */
    UserTaskResponse createUserTask(NewUserTaskRequest newUserTaskRequest);

    /**
     * Partially updates an existing user-task association.
     *
     * @param id The unique identifier of the UserTask to update.
     * @param updateUserTaskRequest Request object containing fields to update.
     * @return The updated UserTaskResponse object.
     */
    UserTaskResponse partialUpdateUserTask(Long id, UpdateUserTaskRequest updateUserTaskRequest);

    /**
     * Retrieves a list of active tasks assigned to a specific user by user ID.
     *
     * @param userId The unique identifier of the user.
     * @return A list of TaskResponse objects representing active tasks.
     */
    List<TaskResponse> getActiveTasksForUserId(Long userId);

    /**
     * Deletes a user-task association by its unique ID.
     *
     * @param id The unique identifier of the UserTask to delete.
     */
    void deleteUserTask(Long id);
}
