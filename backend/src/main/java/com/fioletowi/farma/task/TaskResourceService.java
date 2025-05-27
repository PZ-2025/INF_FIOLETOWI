package com.fioletowi.farma.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service interface for managing TaskResource entities.
 */
public interface TaskResourceService {

    /**
     * Retrieves a paginated list of all task resources.
     *
     * @param pageable pagination information
     * @return paginated list of TaskResourceResponse
     */
    Page<TaskResourceResponse> findAllTaskResources(Pageable pageable);

    /**
     * Finds a task resource by its ID.
     *
     * @param id the ID of the task resource
     * @return the found TaskResourceResponse
     */
    TaskResourceResponse findTaskResourceById(Long id);

    /**
     * Creates a new task resource from the given request.
     *
     * @param newTaskResourceRequest the request containing task resource data
     * @return the created TaskResourceResponse
     */
    TaskResourceResponse createTaskResource(NewTaskResourceRequest newTaskResourceRequest);

    /**
     * Partially updates an existing task resource identified by ID.
     *
     * @param id                      the ID of the task resource to update
     * @param updateTaskResourceRequest the update request data
     * @return the updated TaskResourceResponse
     */
    TaskResourceResponse partialUpdateTaskResource(Long id, UpdateTaskResourceRequest updateTaskResourceRequest);

    /**
     * Deletes a task resource by its ID.
     *
     * @param id the ID of the task resource to delete
     */
    void deleteTaskResource(Long id);

    /**
     * Retrieves a paginated product report filtered by date range and resource filter.
     *
     * @param from           start date-time of the report period
     * @param to             end date-time of the report period
     * @param resourceFilter  resource filter criteria (e.g. "all")
     * @param pageable       pagination information
     * @return paginated list of ProductRaportResponse
     */
    Page<ProductRaportResponse> getProductRaport(LocalDateTime from, LocalDateTime to, String resourceFilter, Pageable pageable);

}
