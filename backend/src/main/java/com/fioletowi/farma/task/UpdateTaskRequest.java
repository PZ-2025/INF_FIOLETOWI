package com.fioletowi.farma.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Request DTO for updating task details.
 * All fields are optional to allow partial updates.
 */
@Getter
@Setter
@Builder
public class UpdateTaskRequest {

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
}
