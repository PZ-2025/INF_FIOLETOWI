package com.fioletowi.farma.task;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TaskServiceImplTest {

    @Mock TaskRepository taskRepository;
    @Mock TeamRepository teamRepository;
    @Mock Mapper<Task, TaskResponse> taskMapper;
    @Mock EmailService emailService;
    @Mock UserTaskRepository userTaskRepository;
    @Mock TeamMemberRepository teamMemberRepository;
    @Mock UserRepository userRepository;

    @InjectMocks TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskResponse sampleResp;
    private NewTaskRequest newReq;
    private UpdateTaskRequest updReq;

    @BeforeEach
    void setUp() {

        userTaskRepository.deleteAll();
        taskRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // sample Task
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setName("T");
        sampleTask.setTaskProgress(TaskProgress.NOT_STARTED);
        sampleTask.setStartDate(LocalDateTime.now());
        sampleTask.setEndDate(LocalDateTime.now().plusDays(1));

        sampleResp = TaskResponse.builder()
                .id(1L).name("T").taskProgress(TaskProgress.NOT_STARTED)
                .startDate(sampleTask.getStartDate()).endDate(sampleTask.getEndDate())
                .build();

        newReq = NewTaskRequest.builder()
                .name("New").taskProgress(TaskProgress.NOT_STARTED)
                .teamId(2L)
                .startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1))
                .build();

        updReq = UpdateTaskRequest.builder()
                .name("Upd").taskProgress(TaskProgress.COMPLETED)
                .build();

        // mapper
        lenient().when(taskMapper.mapTo(any(Task.class), eq(TaskResponse.class)))
                .thenReturn(sampleResp);
    }

    @Test @DisplayName("findTaskById not found")
    void findByIdNotFound() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.findTaskById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task with id 1 not found");
    }

    @Test @DisplayName("createTask team not found")
    void createTaskTeamNotFound() {
        given(teamRepository.findById(2L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> taskService.createTask(newReq))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Team with id 2 not found");
    }

    @Test @DisplayName("createTask success")
    void createTaskSuccess() {
        Team team = new Team(); team.setId(2L);
        given(teamRepository.findById(2L)).willReturn(Optional.of(team));
        given(taskRepository.save(any(Task.class))).willReturn(sampleTask);

        TaskResponse out = taskService.createTask(newReq);
        assertThat(out.getId()).isEqualTo(1L);
        then(taskRepository).should().save(any(Task.class));
    }

    @Test @DisplayName("partialUpdateTask")
    void partialUpdate() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(sampleTask));
        given(taskRepository.save(sampleTask)).willReturn(sampleTask);

        TaskResponse out = taskService.partialUpdateTask(1L, updReq);
        assertThat(out.getTaskProgress()).isEqualTo(TaskProgress.NOT_STARTED);
    }

    @Test @DisplayName("deleteTask not found")
    void deleteNotFound() {
        given(taskRepository.existsById(1L)).willReturn(false);
        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("deleteTask success")
    void deleteSuccess() {
        given(taskRepository.existsById(1L)).willReturn(true);
        taskService.deleteTask(1L);
        then(taskRepository).should().deleteById(1L);
    }

    @Test @DisplayName("getNumberOfTasks")
    void getCount() {
        given(taskRepository.countByIsArchivedFalseAndTaskProgressIn(anyList()))
                .willReturn(5L);
        var cnt = taskService.getNumberOfTasks();
        assertThat(cnt.getCount()).isEqualTo(5L);
    }

    @Test @DisplayName("findAllTasksByManager")
    void byManager() {
        User mgr = new User(); mgr.setId(10L);
        var auth = Mockito.mock(org.springframework.security.core.Authentication.class);
        given(auth.getPrincipal()).willReturn(mgr);
        Team t = new Team(); t.setId(3L);
        given(teamRepository.findAllByLeaderId(10L)).willReturn(List.of(t));
        given(taskRepository.findAllByTeamId(3L)).willReturn(List.of(sampleTask));

        List<TaskResponse> list = taskService.findAllTasksByManager(auth);
        assertThat(list).hasSize(1);
    }

    @Test @DisplayName("getUnassignedTasksByLeaderId")
    void unassigned() {
        User mgr = new User(); mgr.setId(20L);
        var auth = Mockito.mock(org.springframework.security.core.Authentication.class);
        given(auth.getPrincipal()).willReturn(mgr);
        given(taskRepository.findUnassignedTasksByLeaderId(20L))
                .willReturn(List.of(sampleTask));

        var list = taskService.getUnassignedTasksByLeaderId(auth);
        assertThat(list).hasSize(1);
    }

    @Test @DisplayName("getActiveTaskCountForManager")
    void activeCountForMgr() {
        User mgr = new User(); mgr.setId(30L);
        var auth = Mockito.mock(org.springframework.security.core.Authentication.class);
        given(auth.getPrincipal()).willReturn(mgr);
        given(taskRepository.countByTeamLeaderIdAndIsArchivedFalse(30L))
                .willReturn(7L);

        var cnt = taskService.getActiveTaskCountForManager(auth);
        assertThat(cnt.getCount()).isEqualTo(7L);
    }

    @Test @DisplayName("reviewTask invalid progress")
    void reviewInvalid() {
        given(taskRepository.findById(1L)).willReturn(Optional.of(sampleTask));
        assertThatThrownBy(() -> taskService.reviewTask(1L, TaskProgress.NOT_STARTED))
                .isInstanceOf(IllegalArgumentException.class);
    }
}