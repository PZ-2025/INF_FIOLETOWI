package com.fioletowi.farma.team;

import com.fioletowi.farma.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO representing a Team with its basic details and leader information.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse {

    /**
     * Unique identifier of the team.
     */
    private Long id;

    /**
     * Name of the team.
     */
    private String name;

    /**
     * Leader of the team, represented as a UserResponse DTO.
     */
    private UserResponse leader;

    /**
     * Timestamp when the team was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the team was last updated.
     */
    private LocalDateTime updatedAt;

}
