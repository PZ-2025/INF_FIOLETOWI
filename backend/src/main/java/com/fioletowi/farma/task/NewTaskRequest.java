package com.fioletowi.farma.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for creating a new Task.
 * Contains all necessary information required to create a task entity.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewTaskRequest {

    /**
     * The name of the task.
     * This field is required and must not be blank.
     */
    @NotBlank(message = "Task name is required")
    private String name;

    /**
     * The progress status of the task.
     * This field is required.
     */
    @NotNull(message = "Task progress is required")
    private TaskProgress taskProgress;

    /**
     * Optional description of the task.
     */
    private String description;

    /**
     * Optional additional notes related to the task.
     */
    private String note;

    /**
     * The priority level of the task (e.g., "High", "Medium", "Low").
     */
    private String priority;

    /**
     * ID of the team assigned to the task.
     * This field is required.
     */
    @NotNull(message = "Team ID is required")
    private Long teamId;

    /**
     * The date and time when the task starts.
     * This field is required.
     */
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    /**
     * The date and time when the task is expected to end.
     * This field is required.
     */
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
}
