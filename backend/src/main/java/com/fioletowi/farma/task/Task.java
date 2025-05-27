package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.team.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a Task in the system.
 * A task is associated with a team and can have assigned users and resources.
 * It contains information about progress, scheduling, and auditing timestamps.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task {

    /**
     * Primary identifier of the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Current progress status of the task.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_progress", nullable = false)
    private TaskProgress taskProgress;

    /**
     * The name/title of the task.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Optional description of the task's purpose or scope.
     */
    @Column(name = "description")
    private String description;

    /**
     * Optional additional notes related to the task.
     */
    @Column(name = "_note")
    private String note;

    /**
     * Optional priority of the task (e.g., High, Medium, Low).
     */
    @Column
    private String priority;

    /**
     * The start date and time of the task.
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * The expected end date and time of the task.
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * The date and time when the task was sent (e.g., scheduled or dispatched).
     */
    @Column
    private LocalDateTime sendDate;

    /**
     * Indicates whether the task is archived.
     */
    private boolean isArchived = false;

    /**
     * The team assigned to this task.
     */
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    /**
     * List of user-task associations linked to this task.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTask> userTasks = new ArrayList<>();

    /**
     * List of resources associated with this task.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskResource> taskResources = new ArrayList<>();

    /**
     * Timestamp indicating when the task was created.
     * Automatically set and not updatable.
     */
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating the last time the task was updated.
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
