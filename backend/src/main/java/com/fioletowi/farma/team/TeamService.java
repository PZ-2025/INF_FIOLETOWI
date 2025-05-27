package com.fioletowi.farma.team;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing Teams.
 */
public interface TeamService {

    /**
     * Retrieves a paginated list of all teams.
     *
     * @param pageable pagination information
     * @return a page of TeamResponse DTOs
     */
    Page<TeamResponse> findAllTeams(Pageable pageable);

    /**
     * Finds a team by its unique identifier.
     *
     * @param id the ID of the team to find
     * @return the TeamResponse DTO for the found team
     */
    TeamResponse findTeamById(Long id);

    /**
     * Creates a new team based on the provided request data.
     *
     * @param newTeamRequest the request containing new team data
     * @return the TeamResponse DTO of the created team
     */
    TeamResponse createTeam(NewTeamRequest newTeamRequest);

    /**
     * Partially updates an existing team with new data.
     *
     * @param id the ID of the team to update
     * @param updateTeamRequest the request containing updated data
     * @return the updated TeamResponse DTO
     */
    TeamResponse partialUpdateTeam(Long id, UpdateTeamRequest updateTeamRequest);

    /**
     * Deletes a team by its unique identifier.
     *
     * @param id the ID of the team to delete
     */
    void deleteTeam(Long id);

    /**
     * Retrieves the total number of teams.
     *
     * @return a TeamCountResponse containing the count of teams
     */
    TeamCountResponse getNumberOfTeams();

}
