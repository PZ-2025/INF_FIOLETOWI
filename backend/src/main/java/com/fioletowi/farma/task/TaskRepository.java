package com.fioletowi.farma.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link Task} entities.
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {

    /**
     * Returns a paginated list of all tasks.
     *
     * @param pageable pagination information
     * @return page of tasks
     */
    Page<Task> findAll(Pageable pageable);

    /**
     * Returns a list of tasks that are not archived.
     *
     * @return list of active (non-archived) tasks
     */
    List<Task> findByIsArchivedFalse();

    /**
     * Counts non-archived tasks that match any of the given progress statuses.
     *
     * @param allowedStatuses list of allowed task progress values
     * @return number of matching tasks
     */
    long countByIsArchivedFalseAndTaskProgressIn(List<TaskProgress> allowedStatuses);

    /**
     * Counts tasks assigned to a specific user with a given status and falling within the specified date range.
     *
     * @param userId user ID
     * @param status task status
     * @param from start date-time
     * @param to end date-time
     * @return number of matching tasks
     */
    @Query("""
      SELECT COUNT(t)
        FROM Task t
        JOIN UserTask ut ON ut.task.id = t.id
       WHERE ut.user.id = :userId
         AND t.taskProgress = :status
         AND t.endDate BETWEEN :from AND :to
      """)
    long countByUserAndStatus(Long userId, TaskProgress status, LocalDateTime from, LocalDateTime to);

    /**
     * Counts tasks for a specific team with a given status and within a specified date range.
     *
     * @param teamId team ID
     * @param status task status
     * @param from start date-time
     * @param to end date-time
     * @return number of matching tasks
     */
    @Query("""
      SELECT COUNT(t)
        FROM Task t
        JOIN Team tm ON tm.id = t.team.id
       WHERE t.taskProgress = :status
         AND t.endDate BETWEEN :from AND :to
      """)
    long countByTeamAndStatus(Long teamId, TaskProgress status, LocalDateTime from, LocalDateTime to);

    /**
     * Counts tasks for all teams led by a specific leader with a given status and within a specified date range.
     *
     * @param leaderId leader (manager) ID
     * @param status task status
     * @param from start date-time
     * @param to end date-time
     * @return number of matching tasks
     */
    @Query("""
      SELECT COUNT(t)
        FROM Task t
        JOIN Team team ON team.id = t.team.id
       WHERE team.leader.id = :leaderId
         AND t.taskProgress = :status
         AND t.endDate BETWEEN :from AND :to
      """)
    long countByLeaderAndStatus(Long leaderId, TaskProgress status, LocalDateTime from, LocalDateTime to);

    /**
     * Returns all tasks assigned to a given team.
     *
     * @param teamId team ID
     * @return list of tasks
     */
    List<Task> findAllByTeamId(Long teamId);

    /**
     * Finds all non-archived tasks managed by a specific leader that have not yet been assigned to any user.
     *
     * @param leaderId leader (manager) ID
     * @return list of unassigned tasks
     */
    @Query("""
    SELECT t FROM Task t
    WHERE t.team.leader.id = :leaderId
    AND t.id NOT IN (
        SELECT ut.task.id FROM UserTask ut
    )
    AND t.isArchived = false
    """)
    List<Task> findUnassignedTasksByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * Counts all non-archived tasks led by a specific team leader.
     *
     * @param leaderId leader ID
     * @return count of tasks
     */
    Long countByTeamLeaderIdAndIsArchivedFalse(Long leaderId);
}
