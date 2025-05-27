package com.fioletowi.farma.report;

import lombok.*;

/**
 * Data Transfer Object (DTO) representing the efficiency report for a team.
 * <p>
 * Contains aggregated statistics such as task completion counts, number of team members,
 * team leader information, and efficiency rate within a specified period.
 * </p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamReportResponse {

    /**
     * Unique identifier of the team.
     */
    private Long teamId;

    /**
     * Name of the team.
     */
    private String teamName;

    /**
     * Full name of the team's leader.
     */
    private String leaderName;

    /**
     * Number of members in the team.
     */
    private long membersCount;

    /**
     * Number of tasks completed and accepted by the team.
     */
    private long acceptedCount;

    /**
     * Number of tasks completed but terminated by the team.
     */
    private long terminatedCount;

    /**
     * Number of tasks failed by the team.
     */
    private long failedCount;

    /**
     * Total number of tasks (accepted + terminated) by the team.
     */
    private long tasksCount;

    /**
     * Efficiency rate calculated as (tasksCount / (tasksCount + failedCount)).
     */
    private double efficiencyRate;

}
