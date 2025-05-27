package com.fioletowi.farma.seeder;

import com.fioletowi.farma.resource.Expense;
import com.fioletowi.farma.resource.ExpenseRepository;
import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Data seeder for Expense entities.
 *
 * <p>This component seeds initial expense records into the database upon application startup,
 * but only if no expenses currently exist and the application is not running in the "test" profile.</p>
 *
 * <p>It first fetches all existing resources, and if resources exist, it creates two example expenses
 * (one income and one expense) linked to the first resource found.</p>
 *
 * <p>This seeder runs with order 3, after other seeders with lower order values.</p>
 */
@Component
@AllArgsConstructor
@Order(3)
@Profile("!test")
public class ExpenseSeeder implements CommandLineRunner {

    private final ExpenseRepository expenseRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (expenseRepository.count() != 0)
            return;

        // Retrieve all resources, skip seeding if none exist
        List<Resource> resources = StreamSupport.stream(resourceRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        if (resources.isEmpty()) {
            System.out.println("No resources found. Skipping Expense seeding.");
            return;
        }

        // Use the first resource for seeding example expenses
        Resource resource = resources.get(0);

        Expense expense1 = new Expense();
        expense1.setName("Income Example");
        expense1.setDescription("Sample income record");
        expense1.setResource(resource);
        expense1.setQuantity(BigDecimal.valueOf(10));
        // Positive cost value represents income
        expense1.setCost(new BigDecimal("1000.50"));
        expense1.setCreatedAt(LocalDateTime.now());

        Expense expense2 = new Expense();
        expense2.setName("Expense Example");
        expense2.setDescription("Sample expense record");
        expense2.setResource(resource);
        expense2.setQuantity(BigDecimal.valueOf(5));
        // Negative cost value represents expense
        expense2.setCost(new BigDecimal("-500.75"));
        expense2.setCreatedAt(LocalDateTime.now());

        expenseRepository.saveAll(List.of(expense1, expense2));
        System.out.println("Seeded " + expenseRepository.count() + " expenses");
    }
}
