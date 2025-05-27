package com.fioletowi.farma.team;

import com.fioletowi.farma.user.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing team members within the admin context.
 * Provides endpoints for CRUD operations on team members and related queries.
 * Access is limited to users with OWNER or MANAGER roles as specified.
 */
@RestController
@RequestMapping("/admin/team-members")
@RequiredArgsConstructor
public class AdminTeamMemberController {

    private final TeamMemberService teamMemberService;

    /**
     * Retrieves a paginated list of all team members.
     *
     * @param pageable pagination and sorting parameters.
     * @return a page of {@link TeamMemberResponse} objects.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<TeamMemberResponse> getAllTeamMembers(Pageable pageable) {
        return teamMemberService.findAllTeamMembers(pageable);
    }

    /**
     * Retrieves details of a specific team member by ID.
     *
     * @param id the unique identifier of the team member.
     * @return a {@link ResponseEntity} containing the {@link TeamMemberResponse}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamMemberResponse> getTeamMemberById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(teamMemberService.findTeamMemberById(id));
    }

    /**
     * Retrieves the total number of members in a specific team.
     *
     * @param id the unique identifier of the team.
     * @return a {@link ResponseEntity} containing the {@link TeamMemberCountResponse}.
     */
    @GetMapping("/{id}/count")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamMemberCountResponse> getTeamMembersNumber(@PathVariable("id") Long id) {
        return ResponseEntity.ok(teamMemberService.getTeamMembersNumber(id));
    }

    /**
     * Retrieves a list of users who are members of a specific team.
     *
     * @param teamId the ID of the team.
     * @return a {@link ResponseEntity} containing a list of {@link UserResponse}.
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserResponse>> getTeamMembersByTeamId(@PathVariable("id") Long teamId) {
        return ResponseEntity.ok(teamMemberService.getTeamMembersByTeamId(teamId));
    }

    /**
     * Adds a new member to a team.
     *
     * @param newTeamMemberRequest the request payload containing new member details.
     * @return a {@link ResponseEntity} containing the created {@link TeamMemberResponse}.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamMemberResponse> addTeamMember(@RequestBody @Valid NewTeamMemberRequest newTeamMemberRequest) {
        return ResponseEntity.ok(teamMemberService.addTeamMember(newTeamMemberRequest));
    }

    /**
     * Partially updates a team member with the specified fields.
     *
     * @param id the ID of the team member to update.
     * @param updateTeamMemberRequest the request payload with update fields.
     * @return a {@link ResponseEntity} containing the updated {@link TeamMemberResponse}.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TeamMemberResponse> partialUpdateTeamMember(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTeamMemberRequest updateTeamMemberRequest
    ) {
        return ResponseEntity.ok(teamMemberService.partialUpdateTeamMember(id, updateTeamMemberRequest));
    }

    /**
     * Deletes a team member by ID.
     * Only users with the OWNER role are authorized to delete team members.
     *
     * @param id the ID of the team member to delete.
     * @return a {@link ResponseEntity} with no content.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable("id") Long id) {
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Removes a user from a specified team.
     *
     * @param userId the ID of the user to remove.
     * @param teamId the ID of the team.
     * @return a {@link ResponseEntity} with no content.
     */
    @DeleteMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<Void> removeUserFromTeam(
            @RequestParam @NotNull @Min(1) Long userId,
            @RequestParam @NotNull @Min(1) Long teamId
    ) {
        teamMemberService.removeUserFromTeam(userId, teamId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Counts the number of team members managed by the authenticated manager.
     *
     * @param authentication the authentication object representing the current user.
     * @return a {@link ResponseEntity} containing the {@link TeamMemberCountResponse}.
     */
    @GetMapping("/manager/count")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<TeamMemberCountResponse> countByLeader(Authentication authentication) {
        return ResponseEntity.ok(teamMemberService.countByLeader(authentication));
    }

}
