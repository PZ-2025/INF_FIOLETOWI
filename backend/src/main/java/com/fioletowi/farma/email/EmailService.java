package com.fioletowi.farma.email;

import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service responsible for sending various types of emails asynchronously.
 * <p>
 * Utilizes Thymeleaf templates for email content rendering and Spring's JavaMailSender
 * for sending MIME emails.
 * </p>
 * <p>
 * Supports emails for hiring notifications, task assignments, task status updates,
 * team assignments, password reset, and more.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * Sends a hiring notification email to the specified user.
     *
     * @param user the user who has been hired
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendHiringEmail(User user) throws MessagingException {
        Map<String, Object> props = Map.of(
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "hired_at", user.getHiredAt()
        );
        sendTemplateEmail(user.getEmail(), "You're Hired!", "hired", props);
    }

    /**
     * Sends an email notifying the user of a new task assignment.
     *
     * @param user the user assigned to the task
     * @param task the task assigned
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendNewTaskEmail(User user, Task task) throws MessagingException {
        Map<String, Object> props = Map.of(
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "task_start_date", task.getStartDate(),
                "task_end_date", task.getEndDate(),
                "task_name", task.getName(),
                "task_description", task.getDescription(),
                "task_priority", task.getPriority()
        );
        sendTemplateEmail(user.getEmail(), "You have a new task assigned", "new-task", props);
    }

    /**
     * Sends an email to notify the user that a task is ending soon.
     *
     * @param user the user assigned to the task
     * @param task the task that is ending soon
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendTaskEndingSoonEmail(User user, Task task) throws MessagingException {
        Map<String, Object> props = Map.of(
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "task_end_date", task.getEndDate(),
                "task_name", task.getName(),
                "task_description", task.getDescription(),
                "task_priority", task.getPriority()
        );
        sendTemplateEmail(user.getEmail(), "Task is ending soon", "ending-task", props);
    }

    /**
     * Sends an email to notify the user they have been assigned to a team.
     *
     * @param user the user assigned to the team
     * @param team the team assigned
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendAssignedToTeamEmail(User user, Team team) throws MessagingException {
        Map<String, Object> props = Map.of(
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "team_name", team.getName()
        );
        sendTemplateEmail(user.getEmail(), "You’ve been assigned to a team", "assigned-to-team", props);
    }

    /**
     * Sends an email notifying a leader that a task has been completed.
     * <p>This method is marked TODO in original code.</p>
     *
     * @param leader the leader to notify
     * @param task   the completed task
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendTaskCompletedEmail(User leader, Task task) throws MessagingException {
        Map<String, Object> props = Map.of(
                "task_name", task.getName(),
                "task_description", task.getDescription()
        );
        sendTemplateEmail(leader.getEmail(), "A task has been completed", "task-completed", props);
    }

    /**
     * Sends an email notifying a user that their task has been reviewed.
     *
     * @param user the user whose task has been reviewed
     * @param task the task reviewed
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendTaskReviewedEmail(User user, Task task) throws MessagingException {
        Map<String, Object> props = Map.of(
                "first_name", user.getFirstName(),
                "last_name", user.getLastName(),
                "task_name", task.getName(),
                "task_description", task.getDescription(),
                "review", task.getTaskProgress().name()
        );
        sendTemplateEmail(user.getEmail(), "Your task has been reviewed", "task-reviewed", props);
    }

    /**
     * Sends a password reset email with a reset token.
     *
     * @param email the recipient's email address
     * @param token the password reset token
     * @throws MessagingException if sending the email fails
     */
    @Async
    public void sendForgotPasswordEmail(String email, String token) throws MessagingException {
        Map<String, Object> props = Map.of("token", token);
        sendTemplateEmail(email, "Reset your password", "reset-password", props);
    }

    /**
     * Helper method to send an email with a Thymeleaf template.
     *
     * @param to           recipient email address
     * @param subject      email subject
     * @param templateName name of the Thymeleaf template to use
     * @param variables    variables to pass into the template context
     * @throws MessagingException if sending the email fails
     */
    private void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        context.setVariables(variables);

        helper.setFrom("contact@farma.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(templateEngine.process(templateName, context), true);

        javaMailSender.send(mimeMessage);
    }
}
