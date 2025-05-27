package com.fioletowi.farma.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.resource.Resource;
import com.fioletowi.farma.resource.ResourceRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminTaskResourceControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired ResourceRepository resourceRepository;
    @Autowired TaskResourceRepository taskResourceRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String ownerToken;
    private String managerToken;
    private Long taskId, resourceId;

    @BeforeEach
    void setUp() throws Exception {
        // Wyczyść tabele
        taskResourceRepository.deleteAll();
        taskRepository.deleteAll();
        resourceRepository.deleteAll();
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // 1) OWNER
        User owner = User.builder()
                .firstName("O").lastName("One").email("owner@t.test")
                .password(passwordEncoder.encode("OwnerPass1!"))
                .birthDate(LocalDateTime.now().minusYears(40))
                .hiredAt(LocalDateTime.now()).userRole(UserRole.OWNER)
                .address("A").phoneNumber("+48000000000")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(owner);

        // 2) MANAGER
        User mgr = User.builder()
                .firstName("M").lastName("Ager").email("mgr@t.test")
                .password(passwordEncoder.encode("ManagerPass1!"))
                .birthDate(LocalDateTime.now().minusYears(35))
                .hiredAt(LocalDateTime.now()).userRole(UserRole.MANAGER)
                .address("B").phoneNumber("+48000000001")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(mgr);

        // 3) TEAM
        Team team = new Team();
        team.setName("TeamX");
        team.setLeader(mgr);
        teamRepository.save(team);

        // 4) TASK
        Task t = new Task();
        t.setName("Task1");
        t.setTaskProgress(TaskProgress.COMPLETED_ACCEPTED);
        t.setTeam(team);
        t.setStartDate(LocalDateTime.now().minusDays(1));
        t.setEndDate(LocalDateTime.now());
        taskRepository.save(t);
        taskId = t.getId();

        // 5) RESOURCE
        Resource r = new Resource();
        r.setName("Res1"); r.setQuantity(BigDecimal.valueOf(20));
        r.setResourceType(com.fioletowi.farma.resource.ResourceType.FOR_USE);
        resourceRepository.save(r);
        resourceId = r.getId();

        // 6) AUTH OWNER
        String ownerAuth = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuthRequest.builder()
                                        .email("owner@t.test")
                                        .password("OwnerPass1!")
                                        .build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        ownerToken = objectMapper.readTree(ownerAuth).get("token").asText();

        // 7) AUTH MANAGER
        String mgrAuth = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                AuthRequest.builder()
                                        .email("mgr@t.test")
                                        .password("ManagerPass1!")
                                        .build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        managerToken = objectMapper.readTree(mgrAuth).get("token").asText();
    }

    private Long createTR(String token) throws Exception {
        NewTaskResourceRequest req = NewTaskResourceRequest.builder()
                .taskId(taskId)
                .resourceId(resourceId)
                .quantity(BigDecimal.valueOf(5))
                .taskResourceType(TaskResourceType.ASSIGNED)
                .build();
        String body = objectMapper.writeValueAsString(req);

        String resp = mockMvc.perform(post("/admin/task-resource")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resp);
        return node.get("id").asLong();
    }

    @Test
    void getAllTaskResources_asOwner() throws Exception {
        mockMvc.perform(get("/admin/task-resource")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getAllTaskResources_asManager() throws Exception {
        mockMvc.perform(get("/admin/task-resource")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk());
    }

    @Test
    void createTaskResource_asOwner() throws Exception {
        mockMvc.perform(post("/admin/task-resource")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                NewTaskResourceRequest.builder()
                                        .taskId(taskId)
                                        .resourceId(resourceId)
                                        .quantity(BigDecimal.valueOf(5))
                                        .taskResourceType(TaskResourceType.ASSIGNED)
                                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.task.id").value(taskId))
                .andExpect(jsonPath("$.resource.id").value(resourceId));
    }

    @Test
    void getTaskResourceById_asOwner() throws Exception {
        Long id = createTR(ownerToken);
        mockMvc.perform(get("/admin/task-resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void patchTaskResource_asOwner() throws Exception {
        Long id = createTR(ownerToken);
        mockMvc.perform(patch("/admin/task-resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                UpdateTaskResourceRequest.builder()
                                        .quantity(BigDecimal.valueOf(7))
                                        .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @Test
    void deleteTaskResource_asOwner() throws Exception {
        Long id = createTR(ownerToken);
        mockMvc.perform(delete("/admin/task-resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void productRaport_asManager() throws Exception {
        createTR(managerToken);
        String start = LocalDateTime.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String end = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        mockMvc.perform(get("/admin/task-resource/product-raport")
                        .header("Authorization", "Bearer " + managerToken)
                        .param("startDate", start)
                        .param("endDate", end)
                        .param("resource", "all")
                        .param("page","0").param("size","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}