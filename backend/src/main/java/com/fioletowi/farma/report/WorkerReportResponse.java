package com.fioletowi.farma.report;

import lombok.*;

/**
 * Data Transfer Object (DTO) representing the efficiency report for a worker.
 * <p>
 * Contains aggregated statistics such as task completion counts, number of teams
 * the worker belongs to, status, hiring date, and efficiency rate within a specified period.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReportResponse {

    /**
     * Unique identifier of the worker (user).
     */
    private Long userId;

    /**
     * Full name of the worker.
     */
    private String fullName;

    /**
     * Current status of the worker (e.g., ACTIVE, INACTIVE).
     */
    private String status;

    /**
     * Hiring date formatted as a string.
     */
    private String hiredAt;

    /**
     * Number of teams the worker is a member of.
     */
    private long teamCount;

    /**
     * Number of tasks completed and accepted by the worker.
     */
    private long acceptedCount;

    /**
     * Number of tasks completed but terminated by the worker.
     */
    private long terminatedCount;

    /**
     * Number of tasks failed by the worker.
     */
    private long failedCount;

    /**
     * Total number of tasks (accepted + terminated) by the worker.
     */
    private long tasksCount;

    /**
     * Efficiency rate calculated as (tasksCount / (tasksCount + failedCount)).
     */
    private double efficiencyRate;

}
