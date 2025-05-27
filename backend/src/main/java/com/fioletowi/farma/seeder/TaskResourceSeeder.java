package com.fioletowi.farma.seeder;

import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import com.fioletowi.farma.task.Task;
import com.fioletowi.farma.task.TaskResource;
import com.fioletowi.farma.task.TaskResourceRepository;
import com.fioletowi.farma.task.TaskResourceType;
import com.fioletowi.farma.task.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Data seeder for TaskResource entities.
 *
 * <p>This component seeds initial task resource records into the database on application startup,
 * but only if no task resources currently exist and when the application is not running in the "test" profile.</p>
 *
 * <p>It associates the first available Task with the first available Resource and creates two TaskResource records:
 * one with type ASSIGNED (representing consumed resources) and one with type RETURNED (representing returned resources).</p>
 *
 * <p>This seeder runs with order 6, meaning it runs after seeders with a lower order value.</p>
 */
@Component
@AllArgsConstructor
@Order(6)
@Profile("!test")
public class TaskResourceSeeder implements CommandLineRunner {

    private final TaskResourceRepository taskResourceRepository;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (taskResourceRepository.count() != 0)
            return;

        // Retrieve the first available task
        Task task = taskRepository.findAll().iterator().next();

        // Retrieve all resources
        List<Resource> resources = StreamSupport
                .stream(resourceRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (resources.isEmpty()) {
            System.out.println("No resources found, skipping task resource seeding");
            return;
        }

        // Select the first resource (can be extended to seed multiple resources)
        Resource resource = resources.get(0);

        // Create ASSIGNED task resource record (used)
        TaskResource trAssigned = new TaskResource();
        trAssigned.setTask(task);
        trAssigned.setResource(resource);
        trAssigned.setTaskResourceType(TaskResourceType.ASSIGNED);
        trAssigned.setQuantity(BigDecimal.valueOf(20));
        taskResourceRepository.save(trAssigned);

        // Create RETURNED task resource record (returned)
        TaskResource trReturned = new TaskResource();
        trReturned.setTask(task);
        trReturned.setResource(resource);
        trReturned.setTaskResourceType(TaskResourceType.RETURNED);
        trReturned.setQuantity(BigDecimal.valueOf(5));
        taskResourceRepository.save(trReturned);

        System.out.println("Seeded " + taskResourceRepository.count() + " task resources");
    }
}
