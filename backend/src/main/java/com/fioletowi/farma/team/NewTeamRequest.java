package com.fioletowi.farma.team;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for creating a new team.
 */
@Getter
@Setter
@Builder
public class NewTeamRequest {

    /**
     * The name of the team.
     */
    @NotBlank(message = "Team name is required")
    private String name;

    /**
     * The ID of the team leader, if available.
     */
    private Long leaderId;
}
