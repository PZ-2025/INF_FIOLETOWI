package com.fioletowi.farma.team;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for adding a new member to a team.
 */
@Getter
@Setter
@Builder
public class NewTeamMemberRequest {

    /**
     * The ID of the team to which the user will be added.
     */
    @NotNull(message = "Team ID is required")
    private Long teamId;

    /**
     * The ID of the user to be added as a team member.
     */
    @NotNull(message = "User ID is required")
    private Long userId;
}
