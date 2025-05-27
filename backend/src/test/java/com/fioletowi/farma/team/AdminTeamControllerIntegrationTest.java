package com.fioletowi.farma.team;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
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
class AdminTeamControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String ownerToken;
    private User manager;

    @BeforeEach
    void setUp() throws Exception {
        // clean
        teamRepository.deleteAll();
        userRepository.deleteAll();

        // seed Owner and Manager
        User owner = User.builder()
                .firstName("Own").lastName("One")
                .email("owner@t.test")
                .password(passwordEncoder.encode("Pass1234!"))
                .birthDate(LocalDateTime.now().minusYears(40))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.OWNER)
                .address("A").phoneNumber("000")
                .allowNotifications(true).isArchived(false).efficiency(1.0)
                .build();
        userRepository.save(owner);

        manager = User.builder()
                .firstName("Man").lastName("Two")
                .email("mgr@t.test")
                .password(passwordEncoder.encode("Pass1234!"))
                .birthDate(LocalDateTime.now().minusYears(35))
                .hiredAt(LocalDateTime.now())
                .userRole(UserRole.MANAGER)
                .address("B").phoneNumber("111")
                .allowNotifications(true).isArchived(false).efficiency(0.8)
                .build();
        userRepository.save(manager);

        // seed 5 teams
        IntStream.rangeClosed(1,5).forEach(i -> {
            Team t = new Team();
            t.setName("Team"+i);
            if (i==1) t.setLeader(manager);
            teamRepository.save(t);
        });

        // login owner
        var auth = AuthRequest.builder()
                .email("owner@t.test")
                .password("Pass1234!")
                .build();
        var resp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getContentAsString();
        ownerToken = objectMapper.readTree(resp).get("token").asText();
        assertThat(ownerToken).isNotBlank();
    }

    @Test
    void getAllTeams() throws Exception {
        mockMvc.perform(get("/admin/teams")
                        .header("Authorization","Bearer "+ownerToken)
                        .param("page","0").param("size","3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.page.totalElements").value(5));
    }

    @Test
    void getTeamById() throws Exception {
        Long id = StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                .findFirst().map(Team::getId).orElseThrow();
        mockMvc.perform(get("/admin/teams/{id}", id)
                        .header("Authorization","Bearer "+ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void getTeamCount() throws Exception {
        mockMvc.perform(get("/admin/teams/count")
                        .header("Authorization","Bearer "+ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void createTeam() throws Exception {
        var req = NewTeamRequest.builder()
                .name("Zeta")
                .leaderId(manager.getId())
                .build();
        var body = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/admin/teams")
                        .header("Authorization","Bearer "+ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zeta"))
                .andExpect(jsonPath("$.leader.id").value(manager.getId()));
    }

    @Test
    void partialUpdateTeam() throws Exception {
        Long id = teamRepository.findAll().iterator().next().getId();
        var req = UpdateTeamRequest.builder().name("NewName").build();
        var body = objectMapper.writeValueAsString(req);

        mockMvc.perform(patch("/admin/teams/{id}", id)
                        .header("Authorization","Bearer "+ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void deleteTeam() throws Exception {
        Long id = teamRepository.findAll().iterator().next().getId();
        mockMvc.perform(delete("/admin/teams/{id}", id)
                        .header("Authorization","Bearer "+ownerToken))
                .andExpect(status().isNoContent());
        assertThat(teamRepository.existsById(id)).isFalse();
    }
}
