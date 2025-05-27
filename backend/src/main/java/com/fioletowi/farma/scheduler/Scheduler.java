package com.fioletowi.farma.scheduler;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.task.*;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler component responsible for periodic tasks related to users and tasks management.
 *
 * <p>Runs scheduled jobs daily at midnight.</p>
 */
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskServiceImpl taskService;
    private final UserTaskRepository userTaskRepository;
    private final EmailService emailService;

    /**
     * Scheduled job that runs daily at midnight to:
     * <ul>
     *   <li>Delete users who were created more than 14 days ago and have not been hired.</li>
     *   <li>Archive tasks that ended more than 14 days ago and are not already archived.</li>
     * </ul>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void task() {
        // Delete users not hired within 14 days of creation
        List<User> users = userRepository.findByHiredAtIsNull();
        for (User user : users) {
            if (user.getCreatedAt().plusDays(14).isBefore(LocalDateTime.now())) {
                userRepository.delete(user);
            }
        }

        // Archive tasks ended more than 14 days ago
        List<Task> tasks = taskRepository.findByIsArchivedFalse();
        for (Task task : tasks) {
            if (task.getEndDate().plusDays(14).isBefore(LocalDateTime.now())) {
                taskService.archiveTask(task.getId());
            }
        }
    }

    /**
     * Scheduled job that runs daily at midnight to send email notifications
     * to users about tasks ending within the next day.
     *
     * Excludes tasks in progress states such as COMPLETED, FAILED, CANCELLED, etc.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void sendTaskEndingSoonEmails() {
        List<TaskProgress> excluded = List.of(
                TaskProgress.COMPLETED,
                TaskProgress.COMPLETED_ACCEPTED,
                TaskProgress.COMPLETED_TERMINATED,
                TaskProgress.FAILED,
                TaskProgress.CANCELLED
        );

        LocalDateTime deadline = LocalDateTime.now().plusDays(1);

        List<UserTask> endingSoon = userTaskRepository.findActiveTasksEndingWithinNextDay(LocalDateTime.now(), deadline, excluded);

        for (UserTask userTask : endingSoon) {
            try {
                emailService.sendTaskEndingSoonEmail(userTask.getUser(), userTask.getTask());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}
