package com.fioletowi.farma.task;

import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
import com.fioletowi.farma.resource.ResourceType;
import com.fioletowi.farma.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TaskResourceRepositoryTest {

    @Autowired TaskResourceRepository taskResourceRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired ResourceRepository resourceRepository;

    private Task t;
    private Resource r1, r2;
    private TaskResource tr1, tr2;

    @BeforeEach
    void setUp() {
        taskResourceRepository.deleteAll();
        taskRepository.deleteAll();
        resourceRepository.deleteAll();

        // seed Task
        t = new Task();
        t.setName("T");
        t.setTaskProgress(TaskProgress.COMPLETED_ACCEPTED);
        t.setStartDate(LocalDateTime.now().minusDays(1));
        t.setEndDate(LocalDateTime.now());
        taskRepository.save(t);

        // seed Resources
        r1 = new Resource();
        r1.setName("R1"); r1.setQuantity(BigDecimal.valueOf(10)); r1.setResourceType(ResourceType.FOR_USE);
        resourceRepository.save(r1);

        r2 = new Resource();
        r2.setName("R2"); r2.setQuantity(BigDecimal.valueOf(5)); r2.setResourceType(ResourceType.FOR_USE);
        resourceRepository.save(r2);

        LocalDateTime now = LocalDateTime.now();

        // seed TaskResources
        tr1 = new TaskResource();
        tr1.setTask(t); tr1.setResource(r1);
        tr1.setTaskResourceType(TaskResourceType.ASSIGNED);
        tr1.setQuantity(BigDecimal.valueOf(3));
        taskResourceRepository.save(tr1);

        tr2 = new TaskResource();
        tr2.setTask(t); tr2.setResource(r2);
        tr2.setTaskResourceType(TaskResourceType.RETURNED);
        tr2.setQuantity(BigDecimal.valueOf(2));
        taskResourceRepository.save(tr2);
    }

    @Test @DisplayName("findAll(Pageable) returns paginated")
    void testFindAllPageable() {
        Page<TaskResource> page = taskResourceRepository.findAll(PageRequest.of(0,1));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }

    @Test @DisplayName("findAllByCreatedAtBetweenAndTask_TaskProgress")
    void testFindAllByCreatedAtBetweenAndTaskProgress() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to   = LocalDateTime.now().plusDays(1);
        List<TaskResource> list = taskResourceRepository
                .findAllByCreatedAtBetweenAndTask_TaskProgress(from, to, TaskProgress.COMPLETED_ACCEPTED);
        assertThat(list).hasSize(2);
    }

    @Test @DisplayName("findByTaskIdAndResourceIdAndTaskResourceType")
    void testFindByTaskIdAndResourceIdAndType() {
        List<TaskResource> l1 = taskResourceRepository
                .findByTaskIdAndResourceIdAndTaskResourceType(t.getId(), r1.getId(), TaskResourceType.ASSIGNED);
        assertThat(l1).containsExactly(tr1);

        List<TaskResource> l2 = taskResourceRepository
                .findByTaskIdAndResourceIdAndTaskResourceType(t.getId(), r2.getId(), TaskResourceType.ASSIGNED);
        assertThat(l2).isEmpty();
    }
}