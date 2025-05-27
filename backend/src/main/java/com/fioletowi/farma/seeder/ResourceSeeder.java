package com.fioletowi.farma.seeder;

import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import com.fioletowi.farma.resource.ResourceType;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data seeder for Resource entities.
 *
 * <p>This component seeds initial resource records into the database on application startup,
 * but only if no resources currently exist and when the application is not running in the "test" profile.</p>
 *
 * <p>Two example resources are created: one of type FOR_USE and one of type FOR_SELL.</p>
 *
 * <p>This seeder runs with order 2, meaning it runs before other seeders with a higher order value.</p>
 */
@Component
@AllArgsConstructor
@Order(2)
@Profile("!test")
public class ResourceSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (resourceRepository.count() != 0) {
            return;
        }

        Resource resourceForUse = new Resource();
        resourceForUse.setName("Resource For Use");
        resourceForUse.setQuantity(BigDecimal.valueOf(100));
        resourceForUse.setResourceType(ResourceType.FOR_USE);

        Resource resourceForSell = new Resource();
        resourceForSell.setName("Resource For Sell");
        resourceForSell.setQuantity(BigDecimal.valueOf(50));
        resourceForSell.setResourceType(ResourceType.FOR_SELL);

        resourceRepository.saveAll(List.of(resourceForUse, resourceForSell));
        System.out.println("Seeded " + resourceRepository.count() + " resources");
    }
}
