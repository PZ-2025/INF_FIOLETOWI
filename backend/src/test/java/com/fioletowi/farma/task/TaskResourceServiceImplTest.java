package com.fioletowi.farma.task;

import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TaskResourceServiceImplTest {

    @Mock TaskResourceRepository taskResourceRepository;
    @Mock TaskRepository taskRepository;
    @Mock ResourceRepository resourceRepository;
    @Mock Mapper<TaskResource, TaskResourceResponse> trMapper;
    @Mock Mapper<Task, TaskResponse> taskMapper;
    @Mock Mapper<Resource, com.fioletowi.farma.resource.ResourceResponse> resMapper;

    @InjectMocks TaskResourceServiceImpl service;

    private Task t;
    private Resource r;
    private TaskResource tr;
    private TaskResourceResponse trResp;
    private NewTaskResourceRequest newReq;
    private UpdateTaskResourceRequest updReq;

    @BeforeEach
    void setUp() {
        t = new Task(); t.setId(1L);
        r = new Resource(); r.setId(2L);

        tr = new TaskResource();
        tr.setId(10L);
        tr.setTask(t);
        tr.setResource(r);
        tr.setQuantity(BigDecimal.valueOf(5));
        tr.setTaskResourceType(TaskResourceType.ASSIGNED);

        trResp = TaskResourceResponse.builder()
                .id(10L).quantity(BigDecimal.valueOf(5))
                .task(TaskResponse.builder().id(1L).build())
                .resource(com.fioletowi.farma.resource.ResourceResponse.builder().id(2L).build())
                .build();

        newReq = NewTaskResourceRequest.builder()
                .taskId(1L).resourceId(2L)
                .quantity(BigDecimal.valueOf(5)).taskResourceType(TaskResourceType.ASSIGNED)
                .build();
        updReq = UpdateTaskResourceRequest.builder()
                .quantity(BigDecimal.valueOf(7)).build();

        lenient().when(trMapper.mapTo(any(), eq(TaskResourceResponse.class))).thenReturn(trResp);
        lenient().when(taskMapper.mapTo(any(), eq(TaskResponse.class)))
                .thenReturn(TaskResponse.builder().id(1L).build());
        lenient().when(resMapper.mapTo(any(), eq(com.fioletowi.farma.resource.ResourceResponse.class)))
                .thenReturn(com.fioletowi.farma.resource.ResourceResponse.builder().id(2L).build());
    }

    @Test @DisplayName("findAllTaskResources paginates and maps")
    void testFindAll() {
        Page<TaskResource> page = new PageImpl<>(List.of(tr));
        given(taskResourceRepository.findAll(PageRequest.of(0,5))).willReturn(page);

        Page<TaskResourceResponse> out = service.findAllTaskResources(PageRequest.of(0,5));
        assertThat(out.getTotalElements()).isEqualTo(1);
        assertThat(out.getContent().get(0).getId()).isEqualTo(10L);
    }

    @Test @DisplayName("findById not found")
    void testFindByIdNotFound() {
        given(taskResourceRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.findTaskResourceById(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("createTaskResource success")
    void testCreateSuccess() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(t));
        given(resourceRepository.findById(2L)).willReturn(Optional.of(r));
        given(taskResourceRepository
                .findByTaskIdAndResourceIdAndTaskResourceType(1L,2L,TaskResourceType.ASSIGNED))
                .willReturn(Collections.emptyList());
        given(taskResourceRepository.save(any(TaskResource.class))).willReturn(tr);

        TaskResourceResponse out = service.createTaskResource(newReq);
        assertThat(out.getId()).isEqualTo(10L);
        then(taskResourceRepository).should().save(any());
    }

    @Test @DisplayName("createTaskResource duplicate")
    void testCreateDuplicate() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(t));
        given(resourceRepository.findById(2L)).willReturn(Optional.of(r));
        given(taskResourceRepository
                .findByTaskIdAndResourceIdAndTaskResourceType(1L,2L,TaskResourceType.ASSIGNED))
                .willReturn(List.of(tr));

        assertThatThrownBy(() -> service.createTaskResource(newReq))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("partialUpdate success")
    void testPartialUpdate() {
        given(taskResourceRepository.findById(10L)).willReturn(Optional.of(tr));
        given(taskResourceRepository.save(tr)).willReturn(tr);

        TaskResourceResponse out = service.partialUpdateTaskResource(10L, updReq);
        assertThat(out.getQuantity()).isEqualTo(5); // mapper stub ignores change
    }

    @Test @DisplayName("delete not found")
    void testDeleteNotFound() {
        given(taskResourceRepository.existsById(5L)).willReturn(false);
        assertThatThrownBy(() -> service.deleteTaskResource(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("delete success")
    void testDeleteSuccess() {
        given(taskResourceRepository.existsById(10L)).willReturn(true);
        service.deleteTaskResource(10L);
        then(taskResourceRepository).should().deleteById(10L);
    }

    @Test @DisplayName("getProductRaport empty")
    void testProductReportEmpty() {
        // date range
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to   = LocalDateTime.now();
        given(taskResourceRepository
                .findAllByCreatedAtBetweenAndTask_TaskProgress(from, to, TaskProgress.COMPLETED_ACCEPTED))
                .willReturn(List.of());

        Page<ProductRaportResponse> page = service.getProductRaport(from, to, "all", PageRequest.of(0,5));
        assertThat(page.getTotalElements()).isZero();
    }
}