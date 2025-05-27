package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing {@link TaskResource} entities.
 *
 * Provides methods for retrieving task resources with pagination,
 * filtering by creation date and task progress,
 * and querying by task, resource, and resource type.
 */
@Repository
public interface TaskResourceRepository extends CrudRepository<TaskResource, Long> {

    /**
     * Retrieves a paginated list of all TaskResource entities.
     *
     * @param pageable pagination information
     * @return paginated list of TaskResource
     */
    Page<TaskResource> findAll(Pageable pageable);

    /**
     * Finds all TaskResources created between the specified dates
     * and associated with tasks having the specified progress.
     *
     * @param from        start of the creation date range (inclusive)
     * @param to          end of the creation date range (inclusive)
     * @param taskProgress the progress status of the associated tasks
     * @return list of TaskResources matching the criteria
     */
    List<TaskResource> findAllByCreatedAtBetweenAndTask_TaskProgress(LocalDateTime from, LocalDateTime to, TaskProgress taskProgress);

    /**
     * Finds TaskResources by task ID, resource ID, and resource type.
     *
     * @param taskId          ID of the task
     * @param resourceId      ID of the resource
     * @param taskResourceType type of the task resource
     * @return list of matching TaskResources
     */
    List<TaskResource> findByTaskIdAndResourceIdAndTaskResourceType(Long taskId, Long resourceId, TaskResourceType taskResourceType);

}
