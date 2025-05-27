package com.fioletowi.farma.task;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing tasks in the system.
 * <p>
 * Exposes administrative endpoints under `/admin/tasks` for task CRUD operations
 * and queries. Access is secured and restricted to users with roles OWNER or MANAGER,
 * with some endpoints further restricted to OWNER or MANAGER only.
 * </p>
 *
 * <p>Endpoints include:</p>
 * <ul>
 *   <li>Get paginated list of all tasks</li>
 *   <li>Get task by ID</li>
 *   <li>Get total count of tasks</li>
 *   <li>Create a new task</li>
 *   <li>Partial update of a task</li>
 *   <li>Delete a task (OWNER only)</li>
 *   <li>Get tasks assigned to the authenticated manager</li>
 *   <li>Get unassigned tasks for the authenticated manager</li>
 *   <li>Get count of active tasks for the authenticated manager</li>
 *   <li>Review a task by updating its progress (MANAGER only)</li>
 *   <li>Get active tasks by team ID</li>
 * </ul>
 *
 * <p>Security:</p>
 * <ul>
 *   <li>OWNER and MANAGER roles have broad access.</li>
 *   <li>Deletion restricted to OWNER only.</li>
 *   <li>Manager-specific endpoints accessible only by MANAGER role.</li>
 * </ul>
 */
@RestController
@RequestMapping("/admin/tasks")
@RequiredArgsConstructor
public class AdminTaskController {

    private final TaskService taskService;

    /**
     * Retrieves a paginated list of all tasks.
     *
     * @param pageable pagination information
     * @return a page of TaskResponse DTOs
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskService.findAllTasks(pageable);
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id task ID
     * @return the TaskResponse DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    /**
     * Retrieves the total number of tasks.
     *
     * @return a TaskCountResponse containing the count
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskCountResponse> getNumberOfTasks() {
        return ResponseEntity.ok(taskService.getNumberOfTasks());
    }

    /**
     * Creates a new task.
     *
     * @param newTaskRequest the new task details
     * @return ResponseEntity with created TaskResponse and Location header
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid NewTaskRequest newTaskRequest) {
        TaskResponse createdTask = taskService.createTask(newTaskRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }

    /**
     * Partially updates an existing task.
     *
     * @param id task ID to update
     * @param updateTaskRequest partial update details
     * @return the updated TaskResponse
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResponse> partialUpdateTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTaskRequest updateTaskRequest
    ) {
        return ResponseEntity.ok(taskService.partialUpdateTask(id, updateTaskRequest));
    }

    /**
     * Deletes a task by its ID.
     *
     * @param id task ID to delete
     * @return HTTP 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all tasks assigned to the authenticated manager.
     *
     * @param authentication authentication principal
     * @return list of TaskResponse DTOs assigned to the manager
     */
    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<List<TaskResponse>> findAllTasksByManager(Authentication authentication) {
        return ResponseEntity.ok(taskService.findAllTasksByManager(authentication));
    }

    /**
     * Retrieves tasks that are unassigned but relevant to the authenticated manager.
     *
     * @param authentication authentication principal
     * @return list of unassigned TaskResponse DTOs
     */
    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<List<TaskResponse>> getUnassignedTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getUnassignedTasksByLeaderId(authentication));
    }

    /**
     * Retrieves the count of active tasks for the authenticated manager.
     *
     * @param authentication authentication principal
     * @return a TaskCountResponse with the active task count
     */
    @GetMapping("/manager/count")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<TaskCountResponse> getActiveTasksCountForManager(Authentication authentication) {
        return ResponseEntity.ok(taskService.getActiveTaskCountForManager(authentication));
    }

    /**
     * Allows a manager to review and update the progress of a task.
     *
     * @param id task ID
     * @param taskProgress the new progress status of the task
     * @return the updated TaskResponse
     */
    @PatchMapping("/manager/review/{id}")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<TaskResponse> reviewTask(@PathVariable("id") Long id, @RequestParam("taskProgress") TaskProgress taskProgress) {
        return ResponseEntity.ok(taskService.reviewTask(id, taskProgress));
    }

    /**
     * Retrieves all active tasks for a given team by its ID.
     *
     * @param teamId the ID of the team
     * @return list of active TaskResponse DTOs for the team
     */
    @GetMapping("/team/{teamId}/active")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<List<TaskResponse>> getActiveTasksByTeamId(@PathVariable("teamId") Long teamId) {
        return ResponseEntity.ok(taskService.findActiveTasksByTeamId(teamId));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<Page<TaskResponse>> filterTasks(
            @RequestParam(value = "taskProgress", required = false) TaskProgress taskProgress,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "isArchived", defaultValue = "false") boolean isArchived,
            @RequestParam(value = "startDateFrom", required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDateFrom,
            @RequestParam(value = "startDateTo", required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDateTo,
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable
    ) {
        // przekazujemy do serwisu; konwersja LocalDate → LocalDateTime robi się tam
        Page<TaskResponse> page = taskService.filterTasks(
                taskProgress,
                priority,
                isArchived,
                startDateFrom,
                startDateTo,
                name,
                pageable
        );
        return ResponseEntity.ok(page);
    }


}
