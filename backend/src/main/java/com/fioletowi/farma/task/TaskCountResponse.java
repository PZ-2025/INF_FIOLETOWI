package com.fioletowi.farma.task;

import lombok.*;

/**
 * DTO representing a response that contains the count of tasks.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskCountResponse {

    /**
     * The number of tasks.
     */
    private Long count;
}
