package com.fioletowi.farma.resource;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link ResourceService} interface.
 * Provides business logic for managing resources, including CRUD operations and filtering by resource type.
 */
@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final Mapper<Resource, ResourceResponse> resourceMapper;

    /**
     * Retrieves a paginated list of all resources.
     *
     * @param pageable pagination and sorting information
     * @return a page of resource responses
     */
    @Override
    public Page<ResourceResponse> findAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(resource -> resourceMapper.mapTo(resource, ResourceResponse.class));
    }

    /**
     * Finds a resource by its ID.
     *
     * @param id the ID of the resource to find
     * @return the resource response
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    @Override
    public ResourceResponse findResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found"));
        return resourceMapper.mapTo(resource, ResourceResponse.class);
    }

    /**
     * Creates a new resource.
     *
     * @param newResourceRequest the data for the new resource
     * @return the created resource response
     */
    @Override
    public ResourceResponse createResource(NewResourceRequest newResourceRequest) {
        Resource resource = new Resource();
        resource.setName(newResourceRequest.getName());
        resource.setQuantity(newResourceRequest.getQuantity());
        resource.setResourceType(newResourceRequest.getResourceType());
        Resource saved = resourceRepository.save(resource);
        return resourceMapper.mapTo(saved, ResourceResponse.class);
    }

    /**
     * Partially updates an existing resource.
     * Only non-null fields in {@code updateResourceRequest} will be applied.
     *
     * @param id the ID of the resource to update
     * @param updateResourceRequest the update data
     * @return the updated resource response
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    @Override
    public ResourceResponse partialUpdateResource(Long id, UpdateResourceRequest updateResourceRequest) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + id + " not found"));

        Optional.ofNullable(updateResourceRequest.getName()).ifPresent(resource::setName);
        Optional.ofNullable(updateResourceRequest.getQuantity()).ifPresent(resource::setQuantity);
        Optional.ofNullable(updateResourceRequest.getResourceType()).ifPresent(resource::setResourceType);

        Resource updated = resourceRepository.save(resource);
        return resourceMapper.mapTo(updated, ResourceResponse.class);
    }

    /**
     * Deletes a resource by its ID.
     *
     * @param id the ID of the resource to delete
     * @throws ResourceNotFoundException if resource with the given ID does not exist
     */
    @Override
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource with id " + id + " not found");
        }
        resourceRepository.deleteById(id);
    }

    /**
     * Retrieves a paginated list of resources filtered by resource type.
     *
     * @param resourceType the type of resources to filter
     * @param pageable pagination and sorting information
     * @return a page of resource responses filtered by resource type
     */
    @Override
    public Page<ResourceResponse> findAllResourcesByResourceType(ResourceType resourceType, Pageable pageable) {
        return resourceRepository.findAllByResourceType(resourceType, pageable)
                .map(resource -> resourceMapper.mapTo(resource, ResourceResponse.class));
    }
}
