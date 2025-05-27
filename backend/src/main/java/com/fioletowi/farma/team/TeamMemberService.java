package com.fioletowi.farma.team;

import com.fioletowi.farma.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing team members.
 */
public interface TeamMemberService {

    /**
     * Retrieves a paginated list of all team members.
     *
     * @param pageable pagination information
     * @return a page of TeamMemberResponse objects
     */
    Page<TeamMemberResponse> findAllTeamMembers(Pageable pageable);

    /**
     * Finds a team member by its unique ID.
     *
     * @param id the ID of the team member
     * @return the found TeamMemberResponse
     */
    TeamMemberResponse findTeamMemberById(Long id);

    /**
     * Adds a new team member based on the request data.
     *
     * @param newTeamMemberRequest the data for the new team member
     * @return the created TeamMemberResponse
     */
    TeamMemberResponse addTeamMember(NewTeamMemberRequest newTeamMemberRequest);

    /**
     * Partially updates a team member with new data.
     *
     * @param id the ID of the team member to update
     * @param updateTeamMemberRequest the data to update
     * @return the updated TeamMemberResponse
     */
    TeamMemberResponse partialUpdateTeamMember(Long id, UpdateTeamMemberRequest updateTeamMemberRequest);

    /**
     * Deletes a team member by its ID.
     *
     * @param id the ID of the team member to delete
     */
    void deleteTeamMember(Long id);

    /**
     * Removes a user from a specified team.
     *
     * @param userId the ID of the user to remove
     * @param teamId the ID of the team from which to remove the user
     */
    void removeUserFromTeam(Long userId, Long teamId);

    /**
     * Retrieves the number of members in a specific team.
     *
     * @param teamId the ID of the team
     * @return a response containing the count of team members
     */
    TeamMemberCountResponse getTeamMembersNumber(Long teamId);

    /**
     * Gets the list of users that are members of a specific team.
     *
     * @param teamId the ID of the team
     * @return a list of user responses representing the team members
     */
    List<UserResponse> getTeamMembersByTeamId(Long teamId);

    /**
     * Gets the list of teams for which a given user is a member.
     *
     * @param userId the ID of the user
     * @return a list of team responses for the user
     */
    List<TeamResponse> getTeamsForUser(Long userId);

    /**
     * Counts the number of team members under the leader identified by the authentication.
     *
     * @param authentication the current user's authentication
     * @return a response containing the count of team members under the leader
     */
    TeamMemberCountResponse countByLeader(Authentication authentication);
}
