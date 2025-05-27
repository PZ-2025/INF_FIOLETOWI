package com.fioletowi.farma.report;

import lombok.*;

/**
 * Data Transfer Object (DTO) representing the efficiency report for a leader.
 * <p>
 * Contains aggregated statistics such as task completion counts, number of teams led,
 * number of employees under the leader, and efficiency rate within a specified period.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderReportResponse {

    /**
     * Unique identifier of the leader.
     */
    private Long leaderId;

    /**
     * Full name of the leader (first name + last name).
     */
    private String fullName;

    /**
     * Current status of the leader (e.g., ACTIVE, INACTIVE).
     */
    private String status;

    /**
     * Hiring date of the leader formatted as a string.
     */
    private String hiredAt;

    /**
     * Number of teams led by the leader.
     */
    private long teamsCount;

    /**
     * Number of tasks completed and accepted.
     */
    private long acceptedCount;

    /**
     * Number of tasks completed but terminated.
     */
    private long terminatedCount;

    /**
     * Number of tasks that failed.
     */
    private long failedCount;

    /**
     * Total number of tasks (accepted + terminated).
     */
    private long tasksCount;

    /**
     * Efficiency rate calculated as (tasksCount / (tasksCount + failedCount)).
     */
    private double efficiencyRate;

    /**
     * Number of employees reporting to or managed by the leader.
     */
    private long employeesCount;

}
