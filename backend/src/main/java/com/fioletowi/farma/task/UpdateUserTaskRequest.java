package com.fioletowi.farma.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating the association between a user and a task.
 * Contains IDs for task and user to update their relationship.
 */
@Getter
@Setter
@Builder
public class UpdateUserTaskRequest {

    /**
     * ID of the task to associate with the user.
     */
    private Long taskId;

    /**
     * ID of the user to associate with the task.
     */
    private Long userId;
}
