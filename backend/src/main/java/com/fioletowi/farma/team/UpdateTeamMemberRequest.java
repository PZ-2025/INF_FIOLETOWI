package com.fioletowi.farma.team;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating a team member's team or user association.
 */
@Getter
@Setter
@Builder
public class UpdateTeamMemberRequest {

    /**
     * The ID of the new team to assign the member to.
     */
    private Long teamId;

    /**
     * The ID of the new user to associate with the team member.
     */
    private Long userId;
}
