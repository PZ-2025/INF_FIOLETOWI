package com.fioletowi.farma.task;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing UserTask entities in the admin panel.
 * Provides endpoints for CRUD operations, accessible to users with OWNER or MANAGER roles.
 */
@RestController
@RequestMapping("/admin/user-task")
@RequiredArgsConstructor
public class AdminUserTaskController {

    private final UserTaskService userTaskService;

    /**
     * Retrieves a paginated list of all UserTasks.
     *
     * @param pageable pagination information
     * @return a page of UserTaskResponse objects
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<UserTaskResponse> getAllUserTasks(Pageable pageable) {
        return userTaskService.findAllUserTasks(pageable);
    }

    /**
     * Retrieves a UserTask by its ID.
     *
     * @param id the ID of the user task
     * @return the UserTaskResponse if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserTaskResponse> getUserTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userTaskService.findUserTaskById(id));
    }

    /**
     * Creates a new UserTask.
     *
     * @param newUserTaskRequest the request body containing the new user task details
     * @return the created UserTaskResponse
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserTaskResponse> createUserTask(@RequestBody @Valid NewUserTaskRequest newUserTaskRequest) {
        return ResponseEntity.ok(userTaskService.createUserTask(newUserTaskRequest));
    }

    /**
     * Partially updates an existing UserTask.
     *
     * @param id the ID of the user task to update
     * @param updateUserTaskRequest the request body with fields to update
     * @return the updated UserTaskResponse
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<UserTaskResponse> partialUpdateUserTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateUserTaskRequest updateUserTaskRequest
    ) {
        return ResponseEntity.ok(userTaskService.partialUpdateUserTask(id, updateUserTaskRequest));
    }

    /**
     * Deletes a UserTask by its ID.
     *
     * @param id the ID of the user task to delete
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteUserTask(@PathVariable("id") Long id) {
        userTaskService.deleteUserTask(id);
        return ResponseEntity.noContent().build();
    }
}
