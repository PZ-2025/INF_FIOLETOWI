package com.fioletowi.farma.task;

import com.fioletowi.farma.email.EmailService;
import com.fioletowi.farma.exception.ResourceNotFoundException;
import com.fioletowi.farma.mapper.Mapper;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserResponse;
import com.fioletowi.farma.user.UserRole;
import jakarta.mail.MessagingException;
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
class UserTaskServiceImplTest {

    @Mock UserTaskRepository userTaskRepository;
    @Mock TaskRepository taskRepository;
    @Mock UserRepository userRepository;
    @Mock TeamMemberRepository teamMemberRepository;
    @Mock Mapper<UserTask, UserTaskResponse> userTaskMapper;
    @Mock Mapper<Task, TaskResponse> taskMapper;
    @Mock TeamRepository teamRepository;
    @Mock Mapper<User, UserResponse> userMapper;
    @Mock EmailService emailService;

    @InjectMocks UserTaskServiceImpl service;

    private UserTask sampleUT;
    private UserTaskResponse sampleResp;
    private NewUserTaskRequest newReq;
    private UpdateUserTaskRequest updReq;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {

        userTaskRepository.deleteAll();
        taskRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .id(1L)
                .firstName("Worker").lastName("One")
                .email("wrk@t.test").password("x")
                .birthDate(LocalDateTime.now().minusYears(30))
                .hiredAt(LocalDateTime.now()).userRole(UserRole.WORKER)
                .address("").phoneNumber("").allowNotifications(true)
                .isArchived(false).efficiency(1.0)
                .build();

        task = new Task();
        task.setId(2L);
        task.setName("TaskX");
        task.setTaskProgress(TaskProgress.NOT_STARTED);

        sampleUT = new UserTask();
        sampleUT.setId(10L);
        sampleUT.setUser(user);
        sampleUT.setTask(task);

        sampleResp = UserTaskResponse.builder()
                .id(10L)
                .build();

        newReq = NewUserTaskRequest.builder().userId(1L).taskId(2L).build();
        updReq = UpdateUserTaskRequest.builder().userId(1L).taskId(2L).build();

        lenient().when(userTaskMapper.mapTo(any(), eq(UserTaskResponse.class))).thenReturn(sampleResp);
        lenient().when(taskMapper.mapTo(any(), eq(TaskResponse.class))).thenReturn(TaskResponse.builder().id(2L).build());
        lenient().when(userMapper.mapTo(any(), eq(UserResponse.class))).thenReturn(UserResponse.builder().id(1L).build());
    }

    @Test @DisplayName("findAllUserTasks paginates and maps")
    void testFindAllUserTasks() {
        Page<UserTask> page = new PageImpl<>(List.of(sampleUT));
        given(userTaskRepository.findAll(PageRequest.of(0,5))).willReturn(page);

        Page<UserTaskResponse> out = service.findAllUserTasks(PageRequest.of(0,5));
        assertThat(out.getTotalElements()).isEqualTo(1);
    }

    @Test @DisplayName("findUserTaskById not found")
    void testFindByIdNotFound() {
        given(userTaskRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.findUserTaskById(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("createUserTask missing task")
    void testCreateMissingTask() {
        given(taskRepository.findById(2L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.createUserTask(newReq))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("createUserTask missing user")
    void testCreateMissingUser() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task));
        given(userRepository.findById(1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.createUserTask(newReq))
                .isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    @DisplayName("createUserTask success sends email and returns response")
    void testCreateSuccess() throws MessagingException {
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(teamMemberRepository.existsByUserId(user.getId())).willReturn(true);
        given(teamMemberRepository.existsByUserIdAndTeam_LeaderId(user.getId(), user.getId()))
                .willReturn(false);
        given(userTaskRepository.save(any(UserTask.class))).willReturn(sampleUT);

        UserTaskResponse resp = service.createUserTask(newReq);

        then(emailService).should().sendNewTaskEmail(user, task);
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getTask().getId()).isEqualTo(task.getId());
        assertThat(resp.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("partialUpdateUserTask success")
    void testPartialUpdateSuccess() {
        given(userTaskRepository.findById(sampleUT.getId())).willReturn(Optional.of(sampleUT));
        given(taskRepository.findById(task.getId())).willReturn(Optional.of(task));
        given(userTaskRepository.save(sampleUT)).willReturn(sampleUT);

        UserTaskResponse resp = service.partialUpdateUserTask(sampleUT.getId(), updReq);

        assertThat(resp).isSameAs(sampleResp);
        assertThat(resp.getTask().getId()).isEqualTo(task.getId());
    }


    @Test @DisplayName("createUserTask user not in team")
    void testCreateUserNotInTeam() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(teamMemberRepository.existsByUserId(1L)).willReturn(false);

        assertThatThrownBy(() -> service.createUserTask(newReq))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not assigned to any team");
    }

    @Test @DisplayName("createUserTask user is leader")
    void testCreateUserIsLeader() {
        given(taskRepository.findById(2L)).willReturn(Optional.of(task));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(teamMemberRepository.existsByUserId(1L)).willReturn(true);
        given(teamMemberRepository.existsByUserIdAndTeam_LeaderId(1L,1L)).willReturn(true);

        assertThatThrownBy(() -> service.createUserTask(newReq))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot assign a leader");
    }

    @Test @DisplayName("partialUpdateUserTask not found")
    void testPartialUpdateNotFound() {
        given(userTaskRepository.findById(5L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.partialUpdateUserTask(5L, updReq))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("getActiveTasksForUserId")
    void testGetActiveTasksForUserId() {
        given(userTaskRepository.findActiveTasksByUserId(1L))
                .willReturn(List.of(task));
        List<TaskResponse> list = service.getActiveTasksForUserId(1L);
        assertThat(list).hasSize(1);
    }

    @Test @DisplayName("deleteUserTask not found")
    void testDeleteNotFound() {
        given(userTaskRepository.existsById(5L)).willReturn(false);
        assertThatThrownBy(() -> service.deleteUserTask(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("deleteUserTask success")
    void testDeleteSuccess() {
        given(userTaskRepository.existsById(10L)).willReturn(true);
        service.deleteUserTask(10L);
        then(userTaskRepository).should().deleteById(10L);
    }
}