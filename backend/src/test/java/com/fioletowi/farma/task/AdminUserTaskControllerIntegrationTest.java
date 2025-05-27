package com.fioletowi.farma.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.team.Team;
import com.fioletowi.farma.team.TeamMember;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminUserTaskControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired TeamMemberRepository teamMemberRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired UserTaskRepository userTaskRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String ownerToken;
    private String managerToken;
    private Long workerId, taskId;

    @BeforeEach
    void setUp() throws Exception {
        // 0) Wyczyść wszystkie powiązane tabele
        userTaskRepository.deleteAll();
        taskRepository.deleteAll();
        teamMemberRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // 1) OWNER account
        User owner = User.builder()
                .firstName("Owner").lastName("One")
                .email("owner@example.com")
                .password(passwordEncoder.encode("OwnerPass1!"))
                .birthDate(LocalDateTime.of(1980,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.OWNER)
                .address("City").phoneNumber("+48000000000")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(owner);

        // 2) MANAGER account
        User mgr = User.builder()
                .firstName("Manager").lastName("Test")
                .email("manager@example.com")
                .password(passwordEncoder.encode("ManagerPass1!"))
                .birthDate(LocalDateTime.of(1985,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.MANAGER)
                .address("City").phoneNumber("+48000000111")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(mgr);

        // 3) WORKER account
        User worker = User.builder()
                .firstName("Worker").lastName("One")
                .email("worker@example.com")
                .password(passwordEncoder.encode("WorkerPass1!"))
                .birthDate(LocalDateTime.of(1990,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.WORKER)
                .address("City").phoneNumber("+48000000112")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(worker);
        workerId = worker.getId();

        // 4) TEAM led by manager + member
        Team team = new Team();
        team.setName("TeamX");
        team.setLeader(mgr);
        teamRepository.save(team);

        TeamMember tm = new TeamMember();
        tm.setTeam(team);
        tm.setUser(worker);
        teamMemberRepository.save(tm);

        // 5) TASK assigned to team
        Task task = new Task();
        task.setName("Task1");
        task.setTaskProgress(TaskProgress.NOT_STARTED);
        task.setTeam(team);
        task.setStartDate(LocalDateTime.now());
        task.setEndDate(LocalDateTime.now().plusDays(1));
        taskRepository.save(task);
        taskId = task.getId();

        // 6) AUTHENTICATE owner
        AuthRequest authOwner = AuthRequest.builder()
                .email("owner@example.com")
                .password("OwnerPass1!")
                .build();
        String ownerResp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authOwner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();
        ownerToken = objectMapper.readTree(ownerResp).get("token").asText();

        // AUTHENTICATE manager
        AuthRequest authMgr = AuthRequest.builder()
                .email("manager@example.com")
                .password("ManagerPass1!")
                .build();
        String mgrResp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authMgr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();
        managerToken = objectMapper.readTree(mgrResp).get("token").asText();
    }

    private Long createUserTask(String token) throws Exception {
        NewUserTaskRequest req = NewUserTaskRequest.builder()
                .taskId(taskId).userId(workerId).build();
        String body = objectMapper.writeValueAsString(req);

        String resp = mockMvc.perform(post("/admin/user-task")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(resp).get("id").asLong();
    }

    @Test
    void getAllUserTasks_asOwner() throws Exception {
        mockMvc.perform(get("/admin/user-task")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllUserTasks_asManager() throws Exception {
        mockMvc.perform(get("/admin/user-task")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk());
    }

    @Test
    void createUserTask_asOwner() throws Exception {
        NewUserTaskRequest req = NewUserTaskRequest.builder()
                .taskId(taskId).userId(workerId).build();
        mockMvc.perform(post("/admin/user-task")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id").value(taskId))
                .andExpect(jsonPath("$.user.id").value(workerId));
    }

    @Test
    void getUserTaskById_asOwner() throws Exception {
        Long utId = createUserTask(ownerToken);

        mockMvc.perform(get("/admin/user-task/{id}", utId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(utId));
    }

    @Test
    void patchUserTask_asOwner() throws Exception {
        Long utId = createUserTask(ownerToken);

        UpdateUserTaskRequest upd = UpdateUserTaskRequest.builder().build();
        mockMvc.perform(patch("/admin/user-task/{id}", utId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(utId));
    }

    @Test
    void deleteUserTask_asOwner() throws Exception {
        Long utId = createUserTask(ownerToken);

        mockMvc.perform(delete("/admin/user-task/{id}", utId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());
    }
}