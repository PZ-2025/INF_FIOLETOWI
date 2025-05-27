package com.fioletowi.farma.seeder;

import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.task.TaskRepository;
import com.fioletowi.farma.task.UserTask;
import com.fioletowi.farma.task.UserTaskRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds initial UserTask data into the database.
 *
 * <p>This seeder assigns existing users (with IDs 4 and 5) to existing tasks
 * (with IDs 1 and 2). Each user is assigned to each task, creating four UserTask entries.</p>
 *
 * <p>Runs only if no UserTask records exist to prevent duplicate data seeding.</p>
 *
 * <p>Execution order is 7, ensuring that User and Task entities exist before assignment.</p>
 *
 * <p>Active only when the "test" profile is not enabled.</p>
 */
@Component
@AllArgsConstructor
@Order(7)
@Profile("!test")
public class UserTaskSeeder implements CommandLineRunner {

    private final UserTaskRepository userTaskRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userTaskRepository.count() != 0) {
            return;
        }

        // Fetch users with ID 4 and 5
        User user4 = userRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("User with id 4 not found"));
        User user5 = userRepository.findById(5L)
                .orElseThrow(() -> new RuntimeException("User with id 5 not found"));

        // Fetch tasks with ID 1 and 2
        Task task1 = taskRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Task 1 not found"));
        Task task2 = taskRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Task 2 not found"));

        // Assign tasks to users
        UserTask userTask1 = new UserTask();
        userTask1.setTask(task1);
        userTask1.setUser(user4);
        userTaskRepository.save(userTask1);

        UserTask userTask2 = new UserTask();
        userTask2.setTask(task1);
        userTask2.setUser(user5);
        userTaskRepository.save(userTask2);

        UserTask userTask3 = new UserTask();
        userTask3.setTask(task2);
        userTask3.setUser(user4);
        userTaskRepository.save(userTask3);

        UserTask userTask4 = new UserTask();
        userTask4.setTask(task2);
        userTask4.setUser(user5);
        userTaskRepository.save(userTask4);

        System.out.println("Seeded " + userTaskRepository.count() + " user tasks");
    }
}
