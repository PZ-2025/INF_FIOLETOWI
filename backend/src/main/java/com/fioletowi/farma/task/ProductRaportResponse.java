package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.ResourceType;
import lombok.*;

/**
 * DTO representing the product usage report for a given resource within a time period.
 * Used to analyze consumption and returns of resources across tasks.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRaportResponse {

    /**
     * The ID of the resource.
     */
    private Long resourceId;

    /**
     * The name of the resource.
     */
    private String resourceName;

    /**
     * The type of the resource (e.g., MATERIAL, TOOL, etc.).
     */
    private ResourceType resourceType;

    /**
     * Total amount of the resource returned (sum of all TaskResources with type RETURNED).
     */
    private Integer gained;

    /**
     * Total amount of the resource consumed (sum of all TaskResources with type ASSIGNED).
     */
    private Integer consumed;

    /**
     * Net usage of the resource: {@code gained - consumed}.
     */
    private Integer net;

    /**
     * Date of the most recent operation using the resource (max of createdAt in the period),
     * formatted as a string (e.g., "dd.MM.yyyy HH:mm").
     */
    private String lastUsedDate;

    /**
     * Average net usage per task during the period: {@code net / tasksCount}.
     */
    private Double averageUsage;

    /**
     * Number of unique tasks in which the resource was used.
     */
    private Integer tasksCount;

    /**
     * Net usage of the resource during the previous reporting period.
     */
    private Integer previousPeriodNet;

    /**
     * Average net usage per task during the previous reporting period:
     * {@code previousPeriodNet / prevTasksCount}.
     */
    private Double previousAverageUsage;
}
