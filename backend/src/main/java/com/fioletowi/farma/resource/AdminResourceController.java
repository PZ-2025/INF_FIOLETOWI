package com.fioletowi.farma.resource;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing resources in the admin panel.
 * <p>
 * Supports CRUD operations and filtering by resource type.
 * Access restricted to users with roles OWNER and MANAGER (some endpoints OWNER only).
 * </p>
 */
@RestController
@RequestMapping("/admin/resource")
@RequiredArgsConstructor
public class AdminResourceController {

    private final ResourceService resourceService;

    /**
     * Retrieves a paginated list of all resources.
     *
     * @param pageable pagination information
     * @return paginated list of resources
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<ResourceResponse> getAllResources(Pageable pageable) {
        return resourceService.findAllResources(pageable);
    }

    /**
     * Retrieves a resource by its unique ID.
     *
     * @param id the resource ID
     * @return resource details wrapped in ResponseEntity
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(resourceService.findResourceById(id));
    }

    /**
     * Creates a new resource.
     *
     * @param newResourceRequest request body containing resource data
     * @return created resource details wrapped in ResponseEntity
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ResourceResponse> createResource(@RequestBody @Valid NewResourceRequest newResourceRequest) {
        return ResponseEntity.ok(resourceService.createResource(newResourceRequest));
    }

    /**
     * Partially updates an existing resource by ID.
     *
     * @param id the resource ID to update
     * @param updateResourceRequest partial update data
     * @return updated resource details wrapped in ResponseEntity
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ResourceResponse> partialUpdateResource(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateResourceRequest updateResourceRequest) {
        return ResponseEntity.ok(resourceService.partialUpdateResource(id, updateResourceRequest));
    }

    /**
     * Deletes a resource by its ID.
     * <p>
     * Access restricted to users with the OWNER role.
     * </p>
     *
     * @param id the resource ID to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteResource(@PathVariable("id") Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a paginated list of resources filtered by resource type.
     *
     * @param resourceType the resource type to filter by
     * @param pageable pagination information
     * @return filtered and paginated resources wrapped in ResponseEntity
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<Page<ResourceResponse>> findAllResourcesByResourceType(
            @RequestParam ResourceType resourceType, Pageable pageable) {
        Page<ResourceResponse> resources = resourceService.findAllResourcesByResourceType(resourceType, pageable);
        return ResponseEntity.ok(resources);
    }
}
