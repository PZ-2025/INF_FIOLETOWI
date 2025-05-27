package com.fioletowi.farma.team;

import lombok.*;

/**
 * Response DTO representing the count of team members.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamMemberCountResponse {

    private Long count;

}
