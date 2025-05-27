package com.fioletowi.farma.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to provide CRUD operations and additional queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email.
     *
     * @param email the email to search by
     * @return an Optional containing the User if found, or empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves all users in a paginated format.
     *
     * @param pageable pagination information
     * @return a page of users
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Retrieves users filtered by their role, paginated.
     *
     * @param pageable pagination information
     * @param role the user role to filter by
     * @return a page of users with the specified role
     */
    Page<User> findAllByUserRole(Pageable pageable, UserRole role);

    /**
     * Retrieves all users with the specified role.
     *
     * @param role the user role to filter by
     * @return list of users with the specified role
     */
    List<User> findAllByUserRole(UserRole role);

    /**
     * Retrieves users filtered by their status, paginated.
     *
     * @param status the user status to filter by
     * @param pageable pagination information
     * @return a page of users with the specified status
     */
    @Query("SELECT u FROM User u WHERE u.status = :status")
    Page<User> findAllByStatus(@Param("status") UserStatus status, Pageable pageable);

    /**
     * Finds users who have not been hired yet.
     *
     * @return list of users with null hiredAt field
     */
    List<User> findByHiredAtIsNull();

    /**
     * Counts the number of users who are not archived and have been hired.
     *
     * @return count of active hired users
     */
    Long countByIsArchivedFalseAndHiredAtIsNotNull();

    /**
     * Searches users by matching first or last name containing the given filter, case-insensitive.
     *
     * @param workerFilter filter string to match in first or last names
     * @param workerFilter1 same as workerFilter (for query syntax)
     * @return list of matching users
     */
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String workerFilter, String workerFilter1);

    /**
     * Finds team members managed by a given manager who currently have no active (non-archived) tasks assigned.
     *
     * @param managerId the ID of the manager (team leader)
     * @return list of users who are team members without active tasks
     */
    @Query("""
    SELECT DISTINCT u FROM User u
    JOIN TeamMember tm ON tm.user = u
    JOIN Team t ON tm.team = t
    WHERE t.leader.id = :managerId
    AND NOT EXISTS (
        SELECT 1 FROM UserTask ut
        JOIN ut.task task
        WHERE ut.user = u
        AND task.isArchived = false
    )
    """)
    List<User> findTeamMembersWithoutTasksByManagerId(@Param("managerId") Long managerId);

}
