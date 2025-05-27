package com.fioletowi.farma.team;

import com.fioletowi.farma.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing a team member.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamMemberResponse {

    /**
     * Unique identifier of the team member.
     */
    private Long id;

    /**
     * The full team object this member belongs to.
     */
    private TeamResponse team;

    /**
     * The full user object representing the team member.
     */
    private UserResponse user;

    /**
     * Timestamp when this team member was created.
     */
    private LocalDateTime createdAt;
}
