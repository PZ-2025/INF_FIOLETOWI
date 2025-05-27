package com.fioletowi.farma.team;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing teams in the administration context.
 * Provides endpoints for CRUD operations on teams,
 * restricted to users with OWNER or MANAGER roles.
 */
@RestController
@RequestMapping("/admin/teams")
@AllArgsConstructor
public class AdminTeamController {

    private final TeamServiceImpl teamService;

    /**
     * Retrieves a paginated list of all teams.
     *
     * @param pageable pagination and sorting parameters.
     * @return a page of {@link TeamResponse} objects.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<TeamResponse> getAllTeams(Pageable pageable) {
        return teamService.findAllTeams(pageable);
    }

    /**
     * Retrieves a single team by its ID.
     *
     * @param id the unique identifier of the team.
     * @return a {@link ResponseEntity} containing the {@link TeamResponse} if found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(teamService.findTeamById(id));
    }

    /**
     * Returns the total number of teams.
     *
     * @return a {@link ResponseEntity} containing the {@link TeamCountResponse} with the number of teams.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamCountResponse> getTeamCount() {
        return ResponseEntity.ok(teamService.getNumberOfTeams());
    }

    /**
     * Creates a new team.
     *
     * @param newTeamRequest the request payload containing the new team's details.
     * @return a {@link ResponseEntity} with the created {@link TeamResponse}.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamResponse> createTeam(@RequestBody @Valid NewTeamRequest newTeamRequest) {
        return ResponseEntity.ok(teamService.createTeam(newTeamRequest));
    }

    /**
     * Partially updates a team with given fields.
     *
     * @param id the ID of the team to update.
     * @param updateTeamRequest the request payload containing fields to update.
     * @return a {@link ResponseEntity} with the updated {@link TeamResponse}.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamResponse> partialUpdateTeam(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTeamRequest updateTeamRequest
    ) {
        return ResponseEntity.ok(teamService.partialUpdateTeam(id, updateTeamRequest));
    }

    /**
     * Deletes a team by its ID.
     * Only users with the OWNER role are authorized to perform this operation.
     *
     * @param id the ID of the team to delete.
     * @return a {@link ResponseEntity} with no content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Counts the number of teams led by the authenticated manager.
     *
     * @param authentication the authentication object containing the current user details.
     * @return a {@link ResponseEntity} with the {@link TeamCountResponse} for teams led by the user.
     */
    @GetMapping("manager/count")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<TeamCountResponse> countByLeader(Authentication authentication) {
        return ResponseEntity.ok(teamService.countByLeader(authentication));
    }

}
