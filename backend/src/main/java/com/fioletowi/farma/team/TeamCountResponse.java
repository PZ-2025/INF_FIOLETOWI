package com.fioletowi.farma.team;

import lombok.*;

/**
 * Response object representing the count of teams.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamCountResponse {

    /**
     * The total number of teams.
     */
    private long count;

}
