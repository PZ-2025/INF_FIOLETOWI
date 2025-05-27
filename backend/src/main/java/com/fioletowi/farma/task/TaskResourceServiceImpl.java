package com.fioletowi.farma.task;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service implementation for managing TaskResource entities.
 * Provides CRUD operations and reporting functionalities related to task resources.
 */
@Service
@AllArgsConstructor
public class TaskResourceServiceImpl implements TaskResourceService {

    private final TaskResourceRepository taskResourceRepository;
    private final Mapper<TaskResource, TaskResourceResponse> taskResourceMapper;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;
    private final Mapper<Task, TaskResponse> taskMapper;
    private final Mapper<Resource, com.fioletowi.farma.resource.ResourceResponse> resourceMapper;

    /**
     * Retrieves a paginated list of all TaskResources.
     *
     * @param pageable pagination information
     * @return a page of TaskResourceResponse DTOs representing task resources
     */
    @Override
    public Page<TaskResourceResponse> findAllTaskResources(Pageable pageable) {
        return taskResourceRepository.findAll(pageable)
                .map(taskResource -> {
                    TaskResourceResponse response = taskResourceMapper.mapTo(taskResource, TaskResourceResponse.class);
                    response.setTask(taskMapper.mapTo(taskResource.getTask(), TaskResponse.class));
                    response.setResource(resourceMapper.mapTo(taskResource.getResource(), com.fioletowi.farma.resource.ResourceResponse.class));
                    return response;
                });
    }

    /**
     * Finds a TaskResource by its ID.
     *
     * @param id the ID of the TaskResource
     * @return TaskResourceResponse DTO of the found entity
     * @throws ResourceNotFoundException if the TaskResource with the given ID does not exist
     */
    @Override
    public TaskResourceResponse findTaskResourceById(Long id) {
        TaskResource taskResource = taskResourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskResource with id " + id + " not found"));
        TaskResourceResponse response = taskResourceMapper.mapTo(taskResource, TaskResourceResponse.class);
        response.setTask(taskMapper.mapTo(taskResource.getTask(), TaskResponse.class));
        response.setResource(resourceMapper.mapTo(taskResource.getResource(), com.fioletowi.farma.resource.ResourceResponse.class));
        return response;
    }

    /**
     * Creates a new TaskResource entity from the provided data.
     *
     * @param newTaskResourceRequest DTO containing data for creating a new TaskResource,
     *                              including taskId, resourceId, quantity, and taskResourceType
     * @return TaskResourceResponse DTO of the created TaskResource
     * @throws ResourceNotFoundException if the referenced Task or Resource does not exist
     * @throws IllegalArgumentException if a TaskResource with the same task, resource, and type already exists
     */
    @Override
    public TaskResourceResponse createTaskResource(NewTaskResourceRequest newTaskResourceRequest) {
        Task task = taskRepository.findById(newTaskResourceRequest.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + newTaskResourceRequest.getTaskId() + " not found"));
        Resource resource = resourceRepository.findById(newTaskResourceRequest.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + newTaskResourceRequest.getResourceId() + " not found"));

        List<TaskResource> existing = taskResourceRepository
                .findByTaskIdAndResourceIdAndTaskResourceType(task.getId(), resource.getId(), newTaskResourceRequest.getTaskResourceType());
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Resource already added to the task with the same type.");
        }

        TaskResource taskResource = new TaskResource();
        taskResource.setTask(task);
        taskResource.setResource(resource);
        taskResource.setQuantity(newTaskResourceRequest.getQuantity());
        taskResource.setTaskResourceType(newTaskResourceRequest.getTaskResourceType());
        taskResource.setCreatedAt(LocalDateTime.now());

        TaskResource savedTaskResource = taskResourceRepository.save(taskResource);
        TaskResourceResponse response = taskResourceMapper.mapTo(savedTaskResource, TaskResourceResponse.class);
        response.setTask(taskMapper.mapTo(savedTaskResource.getTask(), TaskResponse.class));
        response.setResource(resourceMapper.mapTo(savedTaskResource.getResource(), com.fioletowi.farma.resource.ResourceResponse.class));
        return response;
    }

    /**
     * Partially updates a TaskResource entity identified by its ID.
     * Only the non-null fields in the UpdateTaskResourceRequest are updated.
     *
     * @param id the ID of the TaskResource to update
     * @param updateTaskResourceRequest DTO containing fields to update (taskId, resourceId, quantity)
     * @return updated TaskResourceResponse DTO
     * @throws ResourceNotFoundException if TaskResource, Task, or Resource is not found by ID
     */
    @Override
    public TaskResourceResponse partialUpdateTaskResource(Long id, UpdateTaskResourceRequest updateTaskResourceRequest) {
        TaskResource taskResource = taskResourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskResource with id " + id + " not found"));

        Optional.ofNullable(updateTaskResourceRequest.getTaskId()).ifPresent(taskId -> {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));
            taskResource.setTask(task);
        });

        Optional.ofNullable(updateTaskResourceRequest.getResourceId()).ifPresent(resourceId -> {
            Resource resource = resourceRepository.findById(resourceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Resource with id " + resourceId + " not found"));
            taskResource.setResource(resource);
        });

        Optional.ofNullable(updateTaskResourceRequest.getQuantity()).ifPresent(taskResource::setQuantity);

        TaskResource updatedTaskResource = taskResourceRepository.save(taskResource);
        TaskResourceResponse response = taskResourceMapper.mapTo(updatedTaskResource, TaskResourceResponse.class);
        response.setTask(taskMapper.mapTo(updatedTaskResource.getTask(), TaskResponse.class));
        response.setResource(resourceMapper.mapTo(updatedTaskResource.getResource(), com.fioletowi.farma.resource.ResourceResponse.class));
        return response;
    }

    /**
     * Deletes a TaskResource by its ID.
     *
     * @param id the ID of the TaskResource to delete
     * @throws ResourceNotFoundException if the TaskResource does not exist
     */
    @Override
    public void deleteTaskResource(Long id) {
        if (!taskResourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("TaskResource with id " + id + " not found");
        }
        taskResourceRepository.deleteById(id);
    }

    /**
     * Generates a paginated product report based on TaskResources within a given date range and filtered by resource name.
     * The report calculates quantities gained (returned), consumed (assigned), net usage, number of tasks,
     * average usage per task, last usage date, and comparison with the previous period.
     *
     * @param from           start date-time of the current period (inclusive)
     * @param to             end date-time of the current period (inclusive)
     * @param resourceFilter resource name to filter by (case insensitive), or "all" for no filtering
     * @param pageable       pagination information
     * @return a page of ProductRaportResponse DTOs containing the aggregated report data
     */
    @Override
    public Page<ProductRaportResponse> getProductRaport(LocalDateTime from, LocalDateTime to, String resourceFilter, Pageable pageable) {
        List<TaskResource> currentResources = taskResourceRepository
                .findAllByCreatedAtBetweenAndTask_TaskProgress(from, to, TaskProgress.COMPLETED_ACCEPTED);
//        currentResources.addAll(taskResourceRepository
//                .findAllByCreatedAtBetweenAndTask_TaskProgress(from, to, TaskProgress.COMPLETED_TERMINATED));

        if (!"all".equalsIgnoreCase(resourceFilter)) {
            currentResources.removeIf(tr -> !tr.getResource().getName().equalsIgnoreCase(resourceFilter));
        }

        long days = java.time.Duration.between(from, to).toDays() + 1;
        LocalDateTime previousTo = from.minusSeconds(1);
        LocalDateTime previousFrom = previousTo.minusDays(days - 1);

        List<TaskResource> previousResources = taskResourceRepository
                .findAllByCreatedAtBetweenAndTask_TaskProgress(previousFrom, previousTo, TaskProgress.COMPLETED_ACCEPTED);
        if (!"all".equalsIgnoreCase(resourceFilter)) {
            previousResources.removeIf(tr -> !tr.getResource().getName().equalsIgnoreCase(resourceFilter));
        }

        Map<Long, List<TaskResource>> currentMap = currentResources.stream()
                .collect(java.util.stream.Collectors.groupingBy(tr -> tr.getResource().getId()));
        Map<Long, List<TaskResource>> previousMap = previousResources.stream()
                .collect(java.util.stream.Collectors.groupingBy(tr -> tr.getResource().getId()));

        List<ProductRaportResponse> raportList = new ArrayList<>();
        for (Map.Entry<Long, List<TaskResource>> entry : currentMap.entrySet()) {
            Long resId = entry.getKey();
            List<TaskResource> currentList = entry.getValue();

            int gained = currentList.stream()
                    .filter(tr -> tr.getTaskResourceType() == TaskResourceType.RETURNED)
                    .mapToInt(tr -> tr.getQuantity().intValue())

                    .sum();
            int consumed = currentList.stream()
                    .filter(tr -> tr.getTaskResourceType() == TaskResourceType.ASSIGNED)
                    .mapToInt(tr -> tr.getQuantity().intValue())
                    .sum();

            int net = gained - consumed;

            int tasksCount = (int) currentList.stream().map(tr -> tr.getTask().getId()).distinct().count();
            double averageUsage = tasksCount > 0 ? ((double) net) / tasksCount : 0;

            LocalDateTime maxDate = currentList.stream()
                    .map(TaskResource::getCreatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            String lastUsedDate = (maxDate != null) ? maxDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "-";

            List<TaskResource> prevList = previousMap.getOrDefault(resId, java.util.Collections.emptyList());
            int prevGained = prevList.stream()
                    .filter(tr -> tr.getTaskResourceType() == TaskResourceType.RETURNED)
                    .mapToInt(tr -> tr.getQuantity().intValue())
                    .sum();
            int prevConsumed = prevList.stream()
                    .filter(tr -> tr.getTaskResourceType() == TaskResourceType.ASSIGNED)
                    .mapToInt(tr -> tr.getQuantity().intValue())
                    .sum();
            int previousPeriodNet = prevGained - prevConsumed;
            int prevTasksCount = (int) prevList.stream().map(tr -> tr.getTask().getId()).distinct().count();
            double previousAverageUsage = prevTasksCount > 0 ? ((double) previousPeriodNet) / prevTasksCount : 0;

            TaskResource firstTR = currentList.get(0);
            ProductRaportResponse pr = ProductRaportResponse.builder()
                    .resourceId(resId)
                    .resourceName(firstTR.getResource().getName())
                    .resourceType(firstTR.getResource().getResourceType())
                    .gained(gained)
                    .consumed(consumed)
                    .net(net)
                    .tasksCount(tasksCount)
                    .averageUsage(averageUsage)
                    .lastUsedDate(lastUsedDate)
                    .previousPeriodNet(previousPeriodNet)
                    .previousAverageUsage(previousAverageUsage)
                    .build();
            raportList.add(pr);
        }

        raportList.sort(java.util.Comparator.comparing(ProductRaportResponse::getResourceName));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), raportList.size());
        List<ProductRaportResponse> pageList = raportList.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(pageList, pageable, raportList.size());
    }
}
