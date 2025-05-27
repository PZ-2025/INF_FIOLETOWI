package com.fioletowi.farma.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request DTO for partially updating a TaskResource.
 * Allows updating associated task, resource, and quantity.
 */
@Getter
@Setter
@Builder
public class UpdateTaskResourceRequest {

    /**
     * ID of the task to associate with the TaskResource.
     */
    private Long taskId;

    /**
     * ID of the resource to associate with the TaskResource.
     */
    private Long resourceId;

    /**
     * Quantity of the resource assigned to the task.
     */
    private BigDecimal quantity;
}
