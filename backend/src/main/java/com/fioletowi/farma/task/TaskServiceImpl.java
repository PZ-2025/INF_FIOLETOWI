package com.fioletowi.farma.task;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.team.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of service for managing tasks.
 * Provides CRUD operations as well as additional functionalities such as filtering,
 * retrieving tasks assigned to a manager or team, and email notifications.
 */
@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final Mapper<Task, TaskResponse> taskMapper;
    private final EmailService emailService;
    private final UserTaskRepository userTaskRepository;

    @Override
    public Page<TaskResponse> filterTasks(
            TaskProgress taskProgress,
            String priority,
            boolean isArchived,
            LocalDate startDateFrom,
            LocalDate startDateTo,
            String name,
            Pageable pageable
    ) {
        // konwertujemy zakres dat na LocalDateTime (opcjonalnie)
        LocalDateTime from = startDateFrom != null
                ? startDateFrom.atStartOfDay()
                : LocalDateTime.MIN;
        LocalDateTime to = startDateTo != null
                ? startDateTo.atTime(23, 59, 59)
                : LocalDateTime.MAX;

        Specification<Task> spec = Specification.where(
                // filtr po isArchived zawsze
                (root, q, cb) -> cb.equal(root.get("isArchived"), isArchived)
        );

        if (taskProgress != null) {
            spec = spec.and(
                    (root, q, cb) -> cb.equal(root.get("taskProgress"), taskProgress)
            );
        }
        if (priority != null && !priority.isBlank()) {
            spec = spec.and(
                    (root, q, cb) -> cb.equal(root.get("priority"), priority)
            );
        }
        // zakres dat po startDate
        spec = spec.and(
                (root, q, cb) -> cb.between(root.get("startDate"), from, to)
        );
        if (name != null && !name.isBlank()) {
            spec = spec.and(
                    (root, q, cb) -> cb.like(
                            cb.lower(root.get("name")),
                            "%" + name.toLowerCase() + "%"
                    )
            );
        }

        return taskRepository.findAll(spec, pageable)
                .map(task -> taskMapper.mapTo(task, TaskResponse.class));
    }


    private void recalculateAndSaveEfficiency(Long userId) {
        long accepted = taskRepository.countByUserAndStatus(userId, TaskProgress.COMPLETED_ACCEPTED, null, null);
        long terminated = taskRepository.countByUserAndStatus(userId, TaskProgress.COMPLETED_TERMINATED, null, null);
        long failed = taskRepository.countByUserAndStatus(userId, TaskProgress.FAILED, null, null);
        long totalDone = accepted + terminated;
        double efficiency = (totalDone + failed) > 0
                ? (double) totalDone / (totalDone + failed)
                : 0.0;

        userRepository.findById(userId).ifPresent(user -> {
            user.setEfficiency(efficiency);
            userRepository.save(user);
        });
    }

    /**
     * Returns a paginated list of tasks.
     *
     * @param pageable pagination and sorting parameters
     * @return paged list of tasks mapped to DTO
     */
    @Override
    public Page<TaskResponse> findAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(task -> taskMapper.mapTo(task, TaskResponse.class));
    }

    /**
     * Finds a task by its id.
     *
     * @param id task identifier
     * @return task mapped to DTO
     * @throws ResourceNotFoundException if task with given id does not exist
     */
    @Override
    public TaskResponse findTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return taskMapper.mapTo(task, TaskResponse.class);
    }

    /**
     * Creates a new task based on the provided request data.
     *
     * @param newTaskRequest data for the new task
     * @return created task as DTO
     * @throws ResourceNotFoundException if the specified team does not exist
     */
    @Override
    public TaskResponse createTask(NewTaskRequest newTaskRequest) {
        Task task = new Task();
        task.setName(newTaskRequest.getName());
        task.setTaskProgress(newTaskRequest.getTaskProgress());
        task.setDescription(newTaskRequest.getDescription());
        task.setNote(newTaskRequest.getNote());
        task.setPriority(newTaskRequest.getPriority());
        task.setStartDate(newTaskRequest.getStartDate());
        task.setEndDate(newTaskRequest.getEndDate());

        if (newTaskRequest.getTeamId() != null) {
            Team team = teamRepository.findById(newTaskRequest.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team with id " + newTaskRequest.getTeamId() + " not found"));
            task.setTeam(team);
        }
        // Assuming createdAt and updatedAt are handled automatically by framework
        Task savedTask = taskRepository.save(task);
        return taskMapper.mapTo(savedTask, TaskResponse.class);
    }

    /**
     * Partially updates a task.
     * Only non-null fields from UpdateTaskRequest are set.
     *
     * @param id task id to update
     * @param updateTaskRequest fields to update
     * @return updated task as DTO
     * @throws ResourceNotFoundException if task with given id does not exist
     */
    @Override
    public TaskResponse partialUpdateTask(Long id, UpdateTaskRequest updateTaskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        Optional.ofNullable(updateTaskRequest.getName()).ifPresent(task::setName);
        Optional.ofNullable(updateTaskRequest.getTaskProgress()).ifPresent(task::setTaskProgress);
        Optional.ofNullable(updateTaskRequest.getDescription()).ifPresent(task::setDescription);
        Optional.ofNullable(updateTaskRequest.getNote()).ifPresent(task::setNote);
        Optional.ofNullable(updateTaskRequest.getPriority()).ifPresent(task::setPriority);
        Optional.ofNullable(updateTaskRequest.getStartDate()).ifPresent(task::setStartDate);
        Optional.ofNullable(updateTaskRequest.getEndDate()).ifPresent(task::setEndDate);

        Task updatedTask = taskRepository.save(task);

        List<User> users = userTaskRepository.findUsersByTaskId(id);
        users.forEach(u -> recalculateAndSaveEfficiency(u.getId()));

        return taskMapper.mapTo(updatedTask, TaskResponse.class);
    }

    /**
     * Archives a task by setting its archived flag to true.
     *
     * @param id task id to archive
     * @throws ResourceNotFoundException if task with given id does not exist
     */
    public void archiveTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        task.setArchived(true);
        taskRepository.save(task);
    }

    /**
     * Retrieves the number of active tasks (not archived and with specific progress statuses).
     *
     * @return count of active tasks wrapped in DTO
     */
    @Override
    public TaskCountResponse getNumberOfTasks() {
        return TaskCountResponse.builder()
                .count(taskRepository.countByIsArchivedFalseAndTaskProgressIn(
                        List.of(
                                TaskProgress.NOT_STARTED,
                                TaskProgress.EARLY_PROGRESS,
                                TaskProgress.MIDWAY,
                                TaskProgress.PAST_HALF,
                                TaskProgress.NEAR_COMPLETION,
                                TaskProgress.COMPLETED
                        )
                ))
                .build();
    }

    /**
     * Deletes a task by its id.
     *
     * @param id task id to delete
     * @throws ResourceNotFoundException if task does not exist
     */
    @Override
    public void deleteTask(Long id) {

        List<User> assigned = userTaskRepository.findUsersByTaskId(id);

        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id " + id + " not found");
        }
        taskRepository.deleteById(id);

        assigned.forEach(u -> recalculateAndSaveEfficiency(u.getId()));
    }

    /**
     * Retrieves all tasks assigned to teams where the logged-in user is the leader (manager).
     *
     * @param authentication security context holding logged-in user info
     * @return list of tasks as DTOs
     */
    @Override
    public List<TaskResponse> findAllTasksByManager(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        List<Team> teams = teamRepository.findAllByLeaderId(manager.getId());

        Set<Task> allTasks = new HashSet<>();

        for (Team team : teams) {
            allTasks.addAll(taskRepository.findAllByTeamId(team.getId()));
        }

        return allTasks.stream()
                .filter(task -> !task.isArchived())
                .map(task -> taskMapper.mapTo(task, TaskResponse.class))
                .toList();
    }

    /**
     * Retrieves tasks that are unassigned but belong to teams led by the logged-in leader.
     *
     * @param authentication security context holding logged-in user info
     * @return list of unassigned tasks as DTOs
     */
    @Override
    public List<TaskResponse> getUnassignedTasksByLeaderId(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        return taskRepository.findUnassignedTasksByLeaderId(manager.getId()).stream()
                .map(task -> taskMapper.mapTo(task, TaskResponse.class))
                .toList();
    }

    /**
     * Returns the count of active (non-archived) tasks for the manager.
     *
     * @param authentication security context holding logged-in user info
     * @return task count wrapped in DTO
     */
    public TaskCountResponse getActiveTaskCountForManager(Authentication authentication) {
        User manager = (User) authentication.getPrincipal();
        return TaskCountResponse.builder()
                .count(taskRepository.countByTeamLeaderIdAndIsArchivedFalse(manager.getId()))
                .build();
    }

    /**
     * Updates the progress of a task to a reviewed state and sends notification emails to assigned users.
     *
     * @param id task id to review
     * @param taskProgress new progress state (must be one of COMPLETED_ACCEPTED, FAILED, COMPLETED_TERMINATED)
     * @return updated task as DTO
     * @throws ResourceNotFoundException if task does not exist
     * @throws IllegalArgumentException if taskProgress is not permitted for review
     */
    public TaskResponse reviewTask(Long id, TaskProgress taskProgress) {
        Task task = taskRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Resource with id: " + id + " not found"));

        List<TaskProgress> progresses = List.of(
                TaskProgress.COMPLETED_ACCEPTED,
                TaskProgress.FAILED,
                TaskProgress.COMPLETED_TERMINATED
        );

        if(!progresses.contains(taskProgress)) {
            throw new IllegalArgumentException("You are not permitted to assign " + taskProgress.name() + " to this task");
        }

        task.setTaskProgress(taskProgress);

        List<User> users = userTaskRepository.findUsersByTaskId(task.getId());

        for(User user : users) {
            try {
                emailService.sendTaskReviewedEmail(user, task);
            }
            catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        users.forEach(u -> recalculateAndSaveEfficiency(u.getId()));

        taskRepository.save(task);


        return taskMapper.mapTo(task, TaskResponse.class);
    }

    /**
     * Finds active tasks for a given team by its id.
     * Active means not archived and progress not equal to COMPLETED.
     *
     * @param teamId team identifier
     * @return list of active tasks as DTOs
     */
    @Override
    public List<TaskResponse> findActiveTasksByTeamId(Long teamId) {
        return taskRepository.findAllByTeamId(teamId).stream()
                .filter(task -> !task.isArchived() && !"COMPLETED".equals(task.getTaskProgress().name()))
                .map(task -> taskMapper.mapTo(task, TaskResponse.class))
                .toList();
    }

}