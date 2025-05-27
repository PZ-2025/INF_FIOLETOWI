package com.fioletowi.farma.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fioletowi.farma.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing the association between a user and a task.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskResponse {

    /**
     * Unique identifier of the UserTask entity.
     */
    private Long id;

    /**
     * Full TaskResponse object representing the task assigned to the user.
     */
    private TaskResponse task;

    /**
     * Full UserResponse object representing the user assigned to the task.
     */
    private UserResponse user;

    /**
     * Timestamp when this user-task association was created.
     */
    private LocalDateTime createdAt;

}