package com.fioletowi.farma.team;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating team details.
 */
@Getter
@Setter
@Builder
public class UpdateTeamRequest {

    /**
     * The new name of the team.
     */
    private String name;

    /**
     * The ID of the new team leader.
     */
    private Long leaderId;
}
