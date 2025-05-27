package com.fioletowi.farma.task;

import com.fioletowi.farma.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing UserTask entities.
 */
@Repository
public interface UserTaskRepository extends CrudRepository<UserTask, Long> {

    /**
     * Retrieves a paginated list of all UserTask entities.
     *
     * @param pageable pagination information.
     * @return a page of UserTask entities.
     */
    Page<UserTask> findAll(Pageable pageable);

    /**
     * Finds all active (not archived) tasks assigned to a user by user ID.
     *
     * @param userId the ID of the user.
     * @return a list of active Task entities assigned to the user.
     */
    @Query("SELECT ut.task FROM UserTask ut " +
            "WHERE ut.user.id = :userId AND ut.task.isArchived = false")
    List<Task> findActiveTasksByUserId(Long userId);

    /**
     * Finds all UserTask entities for tasks that are active, not archived,
     * whose end date is within the specified time frame, and whose progress
     * status is not in the excluded list.
     *
     * @param now the current LocalDateTime.
     * @param deadline the deadline LocalDateTime to filter tasks ending before this time.
     * @param excludedStatuses list of TaskProgress statuses to exclude.
     * @return a list of UserTask entities matching the criteria.
     */
    @Query("SELECT ut FROM UserTask ut " +
            "JOIN ut.task t " +
            "WHERE t.isArchived = false " +
            "AND t.endDate > :now " +
            "AND t.endDate < :deadline " +
            "AND t.taskProgress NOT IN :excludedStatuses")
    List<UserTask> findActiveTasksEndingWithinNextDay(
            @Param("now") LocalDateTime now,
            @Param("deadline") LocalDateTime deadline,
            @Param("excludedStatuses") List<TaskProgress> excludedStatuses
    );

    /**
     * Finds all users assigned to a specific task by the task ID.
     *
     * @param taskId the ID of the task.
     * @return a list of User entities assigned to the task.
     */
    @Query("SELECT ut.user FROM UserTask ut WHERE ut.task.id = :taskId")
    List<User> findUsersByTaskId(@Param("taskId") Long taskId);

}
