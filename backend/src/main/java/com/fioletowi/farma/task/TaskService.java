package com.fioletowi.farma.task;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing tasks.
 * Provides methods for creating, retrieving, updating, deleting,
 * and querying tasks with various filters and pagination support.
 */
public interface TaskService {

    /**
     * Retrieves a paginated list of all tasks.
     *
     * @param pageable pagination information
     * @return a page of TaskResponse objects
     */
    Page<TaskResponse> findAllTasks(Pageable pageable);

    /**
     * Finds a task by its unique identifier.
     *
     * @param id the ID of the task
     * @return the TaskResponse representing the found task
     * @throws ResourceNotFoundException if task with given ID does not exist
     */
    TaskResponse findTaskById(Long id);

    /**
     * Creates a new task.
     *
     * @param newTaskRequest the data for creating a new task
     * @return the created TaskResponse
     */
    TaskResponse createTask(NewTaskRequest newTaskRequest);

    /**
     * Partially updates an existing task.
     *
     * @param id the ID of the task to update
     * @param updateTaskRequest fields to update
     * @return the updated TaskResponse
     * @throws ResourceNotFoundException if task with given ID does not exist
     */
    TaskResponse partialUpdateTask(Long id, UpdateTaskRequest updateTaskRequest);

    /**
     * Retrieves the total number of tasks.
     *
     * @return TaskCountResponse containing the count of tasks
     */
    TaskCountResponse getNumberOfTasks();

    /**
     * Deletes a task by its ID.
     *
     * @param id the ID of the task to delete
     * @throws ResourceNotFoundException if task with given ID does not exist
     */
    void deleteTask(Long id);

    /**
     * Finds all tasks managed by the authenticated manager.
     *
     * @param authentication the authentication object representing the current user
     * @return list of TaskResponse objects for tasks managed by the user
     */
    List<TaskResponse> findAllTasksByManager(Authentication authentication);

    /**
     * Retrieves a list of unassigned tasks visible to the leader identified by authentication.
     *
     * @param authentication the authentication object representing the current user
     * @return list of unassigned TaskResponse objects
     */
    List<TaskResponse> getUnassignedTasksByLeaderId(Authentication authentication);

    /**
     * Retrieves the count of active tasks for the authenticated manager.
     *
     * @param authentication the authentication object representing the current user
     * @return TaskCountResponse containing the count of active tasks
     */
    TaskCountResponse getActiveTaskCountForManager(Authentication authentication);

    /**
     * Updates the progress status of a task for review purposes.
     *
     * @param id the ID of the task to review
     * @param taskProgress the new progress status to set
     * @return the updated TaskResponse
     */
    TaskResponse reviewTask(Long id, TaskProgress taskProgress);

    /**
     * Finds active tasks associated with a specific team.
     *
     * @param teamId the ID of the team
     * @return list of active TaskResponse objects for the team
     */
    List<TaskResponse> findActiveTasksByTeamId(Long teamId);

    Page<TaskResponse> filterTasks(
            TaskProgress taskProgress,
            String priority,
            boolean isArchived,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            String name,
            Pageable pageable
    );

}
