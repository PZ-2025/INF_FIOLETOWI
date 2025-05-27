package com.fioletowi.farma.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ResourceRepositoryTest {

    @Autowired
    ResourceRepository resourceRepository;

    @BeforeEach
    void setUp() {
        resourceRepository.deleteAll();
        // Seed with some resources of different types
        IntStream.rangeClosed(1, 5).forEach(i -> {
            Resource resource = new Resource();
            resource.setName("Resource " + i);
            resource.setQuantity(BigDecimal.valueOf(i * 10)); // Just a sample quantity
            resource.setResourceType(i % 2 == 0 ? ResourceType.FOR_SELL : ResourceType.FOR_USE);
            resourceRepository.save(resource);
        });
    }

    @Test
    @DisplayName("findAll(Pageable) returns paginated results")
    void testFindAllPageable() {
        // Test pagination for all resources
        var page = resourceRepository.findAll(PageRequest.of(0, 3)); // Page 0, size 3
        assertThat(page.getTotalElements()).isEqualTo(5);  // We seeded 5 resources
        assertThat(page.getTotalPages()).isEqualTo(2);    // Total pages should be 2 (5 resources, 3 per page)
        assertThat(page.getContent()).hasSize(3);         // The first page should contain 3 resources
    }

    @Test
    @DisplayName("findAllByResourceType returns resources by type")
    void testFindAllByResourceType() {
        // Test pagination for resources of type FOR_USE
        var page = resourceRepository.findAllByResourceType(ResourceType.FOR_USE, PageRequest.of(0, 3));
        assertThat(page.getTotalElements()).isEqualTo(3);  // There should be 3 resources of type FOR_USE
        assertThat(page.getContent().getFirst().getResourceType()).isEqualTo(ResourceType.FOR_USE); // Check resource type
    }

    @Test
    @DisplayName("existsById and deleteById work as expected")
    void testExistsAndDelete() {
        Resource resource = resourceRepository.findAll(PageRequest.of(0, 1))
                .getContent().getFirst();
        Long id = resource.getId();

        // Assert the resource exists before deletion
        assertThat(resourceRepository.existsById(id)).isTrue();

        // Delete the resource
        resourceRepository.deleteById(id);

        // Assert the resource doesn't exist after deletion
        assertThat(resourceRepository.existsById(id)).isFalse();
    }
}
