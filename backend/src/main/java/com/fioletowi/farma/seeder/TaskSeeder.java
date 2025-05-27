package com.fioletowi.farma.seeder;

import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.task.TaskProgress;
import com.fioletowi.farma.task.TaskRepository;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data seeder for Task entities.
 *
 * <p>This component seeds initial Task records into the database on application startup,
 * but only if no tasks currently exist and when the application is not running in the "test" profile.</p>
 *
 * <p>It associates tasks with the first available Team (which is assumed to be already seeded).</p>
 *
 * <p>This seeder runs with order 5, meaning it runs after seeders with a lower order value but before those with a higher one.</p>
 */
@Component
@AllArgsConstructor
@Order(5)
@Profile("!test")
public class TaskSeeder implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) throws Exception {
        if (taskRepository.count() != 0) {
            return;
        }

        // Retrieve the first team – assuming it has already been seeded
        Team team = teamRepository.findAll().iterator().next();

        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setDescription("First test task");
        task1.setTaskProgress(TaskProgress.COMPLETED_ACCEPTED);
        task1.setPriority("HIGH");
        task1.setStartDate(LocalDateTime.now().minusDays(10));
        task1.setEndDate(LocalDateTime.now().minusDays(5));
        task1.setSendDate(LocalDateTime.now().minusDays(4));
        task1.setTeam(team);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setDescription("Second test task");
        task2.setTaskProgress(TaskProgress.COMPLETED_ACCEPTED);
        task2.setPriority("MEDIUM");
        task2.setStartDate(LocalDateTime.now().minusDays(8));
        task2.setEndDate(LocalDateTime.now().minusDays(3));
        task2.setSendDate(LocalDateTime.now().minusDays(2));
        task2.setTeam(team);
        taskRepository.save(task2);

        System.out.println("Seeded " + taskRepository.count() + " tasks");
    }
}
