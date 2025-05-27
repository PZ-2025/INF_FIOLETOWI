package com.fioletowi.farma.team;

import com.fioletowi.farma.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends CrudRepository<TeamMember, Long> {

    /**
     * Retrieves all TeamMember records in a paginated format.
     * @param pageable pagination information
     * @return a page of TeamMember entities
     */
    Page<TeamMember> findAll(Pageable pageable);

    /**
     * Checks if a user belongs to a team whose leader is different from the specified leader.
     * @param userId the user's ID
     * @param leaderId the leader's ID to exclude
     * @return true if such membership exists, false otherwise
     */
    boolean existsByUserIdAndTeam_LeaderIdNot(Long userId, Long leaderId);

    /**
     * Checks if a user is assigned to any team.
     * @param userId the user's ID
     * @return true if the user belongs to at least one team
     */
    boolean existsByUserId(Long userId);

    /**
     * Checks if a user is a leader of any team.
     * @param userId the user's ID
     * @param leaderId the leader's ID (usually the same as userId)
     * @return true if the user is a leader of a team
     */
    boolean existsByUserIdAndTeam_LeaderId(Long userId, Long leaderId);

    /**
     * Counts the number of members in a team by its ID.
     * @param teamId the team's ID
     * @return the number of members in the team
     */
    Long countByTeamId(Long teamId);

    /**
     * Retrieves the list of users assigned to a specific team.
     * @param teamId the team's ID
     * @return a list of users in the team
     */
    @Query("SELECT tm.user FROM TeamMember tm WHERE tm.team.id = :teamId")
    List<User> findUsersByTeamId(@Param("teamId") Long teamId);

    /**
     * Finds the membership of a user in a team by user ID and team ID.
     * @param userId the user's ID
     * @param teamId the team's ID
     * @return an Optional containing the TeamMember if found
     */
    Optional<TeamMember> findByUserIdAndTeamId(Long userId, Long teamId);

    /**
     * Retrieves all team memberships for a given user.
     * @param userId the user's ID
     * @return a list of TeamMember entries associated with the user
     */
    List<TeamMember> findByUserId(Long userId);

    /**
     * Counts how many teams a user belongs to.
     * @param userId the user's ID
     * @return the number of teams the user is part of
     */
    long countByUserId(Long userId);

    /**
     * Counts the number of distinct users assigned to teams led by a specific leader.
     * @param leaderId the leader's user ID
     * @return the count of distinct users under the leader's teams
     */
    @Query("""
        SELECT COUNT(DISTINCT tm.user.id)
        FROM TeamMember tm
        JOIN tm.team t
        WHERE t.leader.id = :leaderId
        """)
    long countDistinctUsersByLeaderId(@Param("leaderId") Long leaderId);

}
