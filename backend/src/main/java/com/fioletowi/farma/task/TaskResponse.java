package com.fioletowi.farma.task;

import com.fioletowi.farma.team.TeamResponse;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Data transfer object (DTO) representing the response structure for a Task.
 * Contains detailed information about the task including status, timing,
 * priority, related team, and descriptive fields.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskResponse {

    /** Unique identifier of the task */
    private Long id;

    /** Current progress/status of the task */
    private TaskProgress taskProgress;

    /** Name/title of the task */
    private String name;

    /** Detailed description of the task */
    private String description;

    /** Additional notes related to the task */
    private String note;

    /** Priority level of the task (e.g. High, Medium, Low) */
    private String priority;

    /** Team associated with the task */
    private TeamResponse team;

    /** Task start date and time */
    private LocalDateTime startDate;

    /** Task end date and time */
    private LocalDateTime endDate;

    /** Date and time the task was sent (e.g. assigned or dispatched) */
    private LocalDateTime sendDate;

    /** Timestamp when the task was created */
    private LocalDateTime createdAt;

    /** Timestamp when the task was last updated */
    private LocalDateTime updatedAt;
}
