package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.ResourceResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO representing a TaskResource with detailed task and resource information.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResourceResponse {

    /**
     * Unique identifier of the task resource.
     */
    private Long id;

    /**
     * Detailed information about the associated task.
     */
    private TaskResponse task;

    /**
     * Detailed information about the associated resource.
     */
    private ResourceResponse resource;

    /**
     * Quantity of the resource assigned or used in the task.
     */
    private BigDecimal quantity;

    /**
     * Type of the task resource (e.g. ASSIGNED, RETURNED).
     */
    private TaskResourceType taskResourceType;

    /**
     * Timestamp when the task resource was created.
     */
    private LocalDateTime createdAt;
}
