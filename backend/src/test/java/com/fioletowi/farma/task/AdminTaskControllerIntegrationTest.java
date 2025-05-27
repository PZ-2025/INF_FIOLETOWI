package com.fioletowi.farma.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.team.Team;
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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminTaskControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String ownerToken;
    private String managerToken;
    private Team team;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Stwórz właściciela
        User owner = User.builder()
                .firstName("Owner").lastName("One")
                .email("owner@example.com")
                .password(passwordEncoder.encode("OwnerPass1!"))
                .birthDate(LocalDateTime.of(1980,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.OWNER)
                .address("City")
                .phoneNumber("+48000000000")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(1.0)
                .build();
        userRepository.save(owner);

        // 2) Stwórz managera
        User mgr = User.builder()
                .firstName("Manager").lastName("Test")
                .email("manager@example.com")
                .password(passwordEncoder.encode("ManagerPass1!"))
                .birthDate(LocalDateTime.of(1985,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.MANAGER)
                .address("City")
                .phoneNumber("+48000000111")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(1.0)
                .build();
        userRepository.save(mgr);

        // 3) Stwórz zespół prowadzony przez managera
        team = new Team();
        team.setName("TeamX");
        team.setLeader(mgr);
        teamRepository.save(team);

        // 4) Zaloguj się jako OWNER i pobierz token
        var authOwner = AuthRequest.builder()
                .email("owner@example.com")
                .password("OwnerPass1!")
                .build();
        var respOwner = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authOwner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ownerToken = objectMapper.readTree(respOwner).get("token").asText();
        assertThat(ownerToken).isNotBlank();

        // 5) Zaloguj się jako MANAGER i pobierz token
        var authMgr = AuthRequest.builder()
                .email("manager@example.com")
                .password("ManagerPass1!")
                .build();
        var respMgr = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authMgr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        managerToken = objectMapper.readTree(respMgr).get("token").asText();
        assertThat(managerToken).isNotBlank();
    }

    @Test
    void getAllTasks_empty() throws Exception {
        mockMvc.perform(get("/admin/tasks")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void createAndGetById() throws Exception {
        // Utwórz nowe zadanie
        NewTaskRequest req = NewTaskRequest.builder()
                .name("T1").taskProgress(TaskProgress.NOT_STARTED)
                .teamId(team.getId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        String body = objectMapper.writeValueAsString(req);

        // POST -> 201 Created
        var mvcRes = mockMvc.perform(post("/admin/tasks")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/admin/tasks/")))
                .andReturn();

        Long id = objectMapper.readTree(mvcRes.getResponse().getContentAsString()).get("id").asLong();

        // GET by id -> 200 OK
        mockMvc.perform(get("/admin/tasks/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("T1"));
    }

    @Test
    void patchAndDelete() throws Exception {
        // najpierw utwórz zadanie
        NewTaskRequest req = NewTaskRequest.builder()
                .name("T2").taskProgress(TaskProgress.NOT_STARTED)
                .teamId(team.getId())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(2))
                .build();
        Long id = objectMapper.readTree(
                mockMvc.perform(post("/admin/tasks")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asLong();

        // PATCH
        UpdateTaskRequest upd = UpdateTaskRequest.builder()
                .name("Updated").taskProgress(TaskProgress.EARLY_PROGRESS).build();
        mockMvc.perform(patch("/admin/tasks/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskProgress").value("EARLY_PROGRESS"));

        // DELETE
        mockMvc.perform(delete("/admin/tasks/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void managerEndpoints() throws Exception {
        // utwórz 3 zadania jako Owner
        IntStream.rangeClosed(1,3).forEach(i -> {
            NewTaskRequest r = NewTaskRequest.builder()
                    .name("M"+i).taskProgress(TaskProgress.NOT_STARTED)
                    .teamId(team.getId())
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(1))
                    .build();
            try {
                mockMvc.perform(post("/admin/tasks")
                                .header("Authorization", "Bearer " + ownerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(r)))
                        .andExpect(status().isCreated());
            } catch (Exception ignored) {}
        });

        // GET manager tasks
        mockMvc.perform(get("/admin/tasks/manager")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // GET unassigned tasks
        mockMvc.perform(get("/admin/tasks/unassigned")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk());

        // GET manager/count
        mockMvc.perform(get("/admin/tasks/manager/count")
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));

        // GET active by team
        mockMvc.perform(get("/admin/tasks/team/{teamId}/active", team.getId())
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }
}