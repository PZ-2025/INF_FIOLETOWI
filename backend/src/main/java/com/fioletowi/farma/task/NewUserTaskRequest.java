package com.fioletowi.farma.task;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for assigning a user to a task.
 * Represents the association between a user and a task.
 */
@Getter
@Setter
@Builder
public class NewUserTaskRequest {

    /**
     * The ID of the task to assign.
     * This field is required.
     */
    @NotNull(message = "Task ID is required")
    private Long taskId;

    /**
     * The ID of the user to assign to the task.
     * This field is required.
     */
    @NotNull(message = "User ID is required")
    private Long userId;
}
