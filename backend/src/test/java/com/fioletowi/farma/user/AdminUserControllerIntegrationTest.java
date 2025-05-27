package com.fioletowi.farma.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.resource.ExpenseRepository;
import com.fioletowi.farma.resource.ResourceRepository;
import com.fioletowi.farma.task.TaskResourceRepository;
import com.fioletowi.farma.task.TaskRepository;
import com.fioletowi.farma.task.UserTaskRepository;
import com.fioletowi.farma.team.TeamMemberRepository;
import com.fioletowi.farma.team.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminUserControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserTaskRepository      userTaskRepo;
    @Autowired TaskResourceRepository taskResourceRepo;
    @Autowired TaskRepository         taskRepo;
    @Autowired TeamMemberRepository   teamMemberRepo;
    @Autowired TeamRepository         teamRepo;
    @Autowired ExpenseRepository      expenseRepo;
    @Autowired ResourceRepository     resourceRepo;
    @Autowired UserRepository         userRepository;
    @Autowired PasswordEncoder        passwordEncoder;

    private String ownerToken;

    @BeforeEach
    void setUp() throws Exception {

        // 1) Stwórz właściciela i 15 workerów
        var owner = User.builder()
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

        IntStream.rangeClosed(1, 15).forEach(i -> {
            var u = User.builder()
                    .firstName("User"+i).lastName("Test")
                    .email("user"+i+"@example.com")
                    .password(passwordEncoder.encode("UserPass1!"))
                    .birthDate(LocalDateTime.of(1990,1,1,0,0))
                    .hiredAt(LocalDateTime.now())
                    .userRole(UserRole.WORKER)
                    .address("City")
                    .phoneNumber("+480000000" + String.format("%02d", i))
                    .allowNotifications(true)
                    .isArchived(false)
                    .efficiency(1.0)
                    .build();
            userRepository.save(u);
        });

        // 2) Zaloguj się i pobierz token
        var auth = AuthRequest.builder()
                .email("owner@example.com")
                .password("OwnerPass1!")
                .build();
        var resp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ownerToken = objectMapper.readTree(resp).get("token").asText();
        assertThat(ownerToken).isNotBlank();
    }

    @Test
    void getAllUsers_defaultPage() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page","0").param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(10))
                // totalElements jest wewnątrz page
                .andExpect(jsonPath("$.page.totalElements").value(16))
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    void getAllUsers_filterByRole_MANAGER() throws Exception {
        // dodaj managera
        var m = User.builder()
                .firstName("Man").lastName("Ager")
                .email("mgr@example.com")
                .password(passwordEncoder.encode("MgrPass1!"))
                .birthDate(LocalDateTime.of(1985,1,1,0,0))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.MANAGER)
                .address("City")
                .phoneNumber("+48000000111")
                .allowNotifications(true)
                .isArchived(false)
                .efficiency(1.0)
                .build();
        userRepository.save(m);

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("role","MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].email").value("mgr@example.com"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void getNumberOfUsers() throws Exception {
        mockMvc.perform(get("/admin/users/count")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(16));
    }

    @Test
    void getUserById_found() throws Exception {
        Long id = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(u -> u.getUserRole() == UserRole.WORKER)
                .map(User::getId)
                .findFirst().orElseThrow();

        mockMvc.perform(get("/admin/users/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }
}
