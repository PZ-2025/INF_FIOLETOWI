package com.fioletowi.farma.task;

import com.fioletowi.farma.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling user-task related endpoints.
 */
@RestController
@RequestMapping("/user-tasks")
@RequiredArgsConstructor
public class UserTaskController {

    private final TaskService taskService;
    private final UserTaskService userTaskService;

    /**
     * Retrieves active tasks assigned to the currently authenticated user.
     *
     * @param authentication The Spring Security authentication object containing user details.
     * @return ResponseEntity containing a list of active TaskResponse objects for the user.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('WORKER')")
    public ResponseEntity<List<TaskResponse>> getActiveTasksForUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<TaskResponse> tasks = userTaskService.getActiveTasksForUserId(user.getId());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Partially updates the task with the given ID using the provided data.
     *
     * @param id the ID of the task to update
     * @param updateTaskRequest the fields to update in the task
     * @return updated task wrapped in a ResponseEntity
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('WORKER')")
    public ResponseEntity<TaskResponse> partialUpdateTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTaskRequest updateTaskRequest
    ) {
        return ResponseEntity.ok(taskService.partialUpdateTask(id, updateTaskRequest));
    }

}
