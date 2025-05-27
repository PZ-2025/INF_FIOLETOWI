package com.fioletowi.farma.task;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * REST controller for managing TaskResource entities in the admin panel.
 * Provides endpoints for CRUD operations and generating reports.
 */
@RestController
@RequestMapping("/admin/task-resource")
@RequiredArgsConstructor
public class AdminTaskResourceController {

    private final TaskResourceService taskResourceService;
//    private final PagedResourcesAssembler<ProductRaportResponse> pagedAssembler;

    /**
     * Retrieves a paginated list of all TaskResources.
     *
     * @param pageable pagination information
     * @return a page of TaskResourceResponse
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public Page<TaskResourceResponse> getAllTaskResources(Pageable pageable) {
        return taskResourceService.findAllTaskResources(pageable);
    }

    /**
     * Retrieves a TaskResource by its ID.
     *
     * @param id the ID of the task resource
     * @return the TaskResourceResponse if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResourceResponse> getTaskResourceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(taskResourceService.findTaskResourceById(id));
    }

    /**
     * Creates a new TaskResource.
     *
     * @param newTaskResourceRequest the request body containing new task resource details
     * @return the created TaskResourceResponse
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResourceResponse> createTaskResource(@RequestBody @Valid NewTaskResourceRequest newTaskResourceRequest) {
        return ResponseEntity.ok(taskResourceService.createTaskResource(newTaskResourceRequest));
    }

    /**
     * Partially updates an existing TaskResource.
     *
     * @param id the ID of the task resource to update
     * @param updateTaskResourceRequest the request body with fields to update
     * @return the updated TaskResourceResponse
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<TaskResourceResponse> partialUpdateTaskResource(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateTaskResourceRequest updateTaskResourceRequest) {
        return ResponseEntity.ok(taskResourceService.partialUpdateTaskResource(id, updateTaskResourceRequest));
    }

    /**
     * Deletes a TaskResource by its ID.
     *
     * @param id the ID of the task resource to delete
     * @return HTTP 204 No Content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER')")
    public ResponseEntity<Void> deleteTaskResource(@PathVariable("id") Long id) {
        taskResourceService.deleteTaskResource(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/product-raport")
//    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
//    public ResponseEntity<PagedModel<EntityModel<ProductRaportResponse>>> getProductRaport(
//            @RequestParam("startDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
//            @RequestParam("endDate")   @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
//            @RequestParam(value = "resource", defaultValue = "all") String resource,
//            Pageable pageable) {
//        LocalDateTime from = startDate.atStartOfDay();
//        LocalDateTime to = endDate.atTime(23, 59, 59);
//        Page<ProductRaportResponse> page = taskResourceService.getProductRaport(from, to, resource, pageable);
//        return ResponseEntity.ok(pagedAssembler.toModel(page));
//    }

    /**
     * Generates a product report for a given date range and optional resource filter.
     *
     * @param startDate the start date in format dd.MM.yyyy
     * @param endDate the end date in format dd.MM.yyyy
     * @param resource the resource filter (or "all")
     * @param pageable pagination information
     * @return a page of ProductRaportResponse
     */
    @GetMapping("/product-raport")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<Page<ProductRaportResponse>> getProductRaport(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate endDate,
            @RequestParam(value = "resource", defaultValue = "all") String resource,
            Pageable pageable) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.atTime(23, 59, 59);
        Page<ProductRaportResponse> page = taskResourceService.getProductRaport(from, to, resource, pageable);
        return ResponseEntity.ok(page);
    }
}
