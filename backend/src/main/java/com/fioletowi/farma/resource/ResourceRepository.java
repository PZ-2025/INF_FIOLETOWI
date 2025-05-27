package com.fioletowi.farma.resource;

import com.fioletowi.farma.task.TaskResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Resource} entities.
 *
 * <p>Extends {@link CrudRepository} to provide CRUD operations and
 * defines additional methods for paginated retrieval of resources.</p>
 *
 * <ul>
 *   <li>{@code findAll(Pageable pageable)} - Retrieves all resources with pagination support.</li>
 *   <li>{@code findAllByResourceType(ResourceType resourceType, Pageable pageable)} - Retrieves all resources of a specific resource type with pagination.</li>
 * </ul>
 */
@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {

    /**
     * Retrieves all resources in a paginated format.
     *
     * @param pageable the pagination information
     * @return a page of resources
     */
    Page<Resource> findAll(Pageable pageable);

    /**
     * Retrieves all resources filtered by the given {@link ResourceType} in a paginated format.
     *
     * @param resourceType the type of the resource to filter by
     * @param pageable the pagination information
     * @return a page of resources matching the specified resource type
     */
    Page<Resource> findAllByResourceType(ResourceType resourceType, Pageable pageable);
}
