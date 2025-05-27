package com.fioletowi.farma.resource;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing resources.
 * Provides methods to create, retrieve, update, delete, and filter resources.
 */
public interface ResourceService {

    /**
     * Retrieves a paginated list of all resources.
     *
     * @param pageable pagination and sorting information
     * @return a page of resource responses
     */
    Page<ResourceResponse> findAllResources(Pageable pageable);

    /**
     * Finds a resource by its unique ID.
     *
     * @param id the ID of the resource to find
     * @return the resource response
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    ResourceResponse findResourceById(Long id);

    /**
     * Creates a new resource.
     *
     * @param newResourceRequest the details of the resource to create
     * @return the created resource response
     */
    ResourceResponse createResource(NewResourceRequest newResourceRequest);

    /**
     * Partially updates an existing resource.
     *
     * @param id the ID of the resource to update
     * @param updateResourceRequest the fields to update
     * @return the updated resource response
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    ResourceResponse partialUpdateResource(Long id, UpdateResourceRequest updateResourceRequest);

    /**
     * Deletes a resource by its ID.
     *
     * @param id the ID of the resource to delete
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    void deleteResource(Long id);

    /**
     * Finds resources filtered by their resource type with pagination.
     *
     * @param resourceType the type of resource to filter by
     * @param pageable pagination and sorting information
     * @return a page of resource responses filtered by resource type
     */
    Page<ResourceResponse> findAllResourcesByResourceType(ResourceType resourceType, Pageable pageable);
}
