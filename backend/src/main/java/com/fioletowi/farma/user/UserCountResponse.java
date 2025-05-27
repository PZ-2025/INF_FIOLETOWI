package com.fioletowi.farma.user;

import lombok.*;

/**
 * Response DTO representing a count of users.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserCountResponse {

    /**
     * The total number of users.
     */
    private Long count;

}
