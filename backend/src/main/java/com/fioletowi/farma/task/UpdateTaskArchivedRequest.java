package com.fioletowi.farma.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request DTO for updating a task’s archived status along with optional task details.
 * Fields are nullable to allow partial updates.
 */
@Getter
@Setter
@Builder
public class UpdateTaskArchivedRequest {

    /**
     * New name of the task.
     */
    private String name;

    /**
     * New progress status of the task.
     */
    private TaskProgress taskProgress;

    /**
     * New description of the task.
     */
    private String description;

    /**
     * New note associated with the task.
     */
    private String note;

    /**
     * New priority level of the task.
     */
    private String priority;

    /**
     * New start date and time of the task.
     */
    private LocalDateTime startDate;

    /**
     * New end date and time of the task.
     */
    private LocalDateTime endDate;

    /**
     * Identifier of the user related to the update (e.g., assigned user).
     */
    private Long userId;
}
