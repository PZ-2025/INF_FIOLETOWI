package com.fioletowi.farma.task;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO for creating a new TaskResource association.
 * Represents the link between a task and a resource, including quantity and type.
 */
@Getter
@Setter
@Builder
public class NewTaskResourceRequest {

    /**
     * The ID of the task to which the resource will be assigned.
     * This field is required.
     */
    @NotNull(message = "Task ID is required")
    private Long taskId;

    /**
     * The ID of the resource being assigned to the task.
     * This field is required.
     */
    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    /**
     * The quantity of the resource to be used in the task.
     * This field is required.
     */
    @NotNull(message = "Quantity is required")
    private BigDecimal quantity;

    /**
     * The type of the task resource (e.g., INPUT or OUTPUT).
     * This field is required.
     */
    @NotNull(message = "Task resource type is required")
    private TaskResourceType taskResourceType;
}
