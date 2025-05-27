package com.fioletowi.farma.team;

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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminTeamMemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private TeamMemberRepository tmRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String ownerToken;
    private Long teamId;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Stwórz właściciela
        User owner = new User();
        owner.setFirstName("O");
        owner.setLastName("W");
        owner.setEmail("own@t.test");
        owner.setPassword(passwordEncoder.encode("Owner1234!"));
        owner.setBirthDate(LocalDateTime.now());
        owner.setHiredAt(LocalDateTime.now());
        owner.setUserRole(UserRole.OWNER);
        owner.setAddress("A");
        owner.setPhoneNumber("000");
        owner.setAllowNotifications(true);
        owner.setIsArchived(false);
        owner.setEfficiency(1.0);
        owner = userRepo.save(owner);

        // 2) Stwórz zespół
        Team team = new Team();
        team.setName("Alpha");
        team.setLeader(owner);
        team = teamRepo.save(team);
        teamId = team.getId();

        // 3) Stwórz kilku workerów
        IntStream.rangeClosed(1, 5).forEach(i -> {
            User u = new User();
            u.setFirstName("U" + i);
            u.setLastName("T");
            u.setEmail("u" + i + "@t.test");
            u.setPassword(passwordEncoder.encode("Pass1234!"));
            u.setBirthDate(LocalDateTime.now());
            u.setHiredAt(LocalDateTime.now());
            u.setUserRole(UserRole.WORKER);
            u.setAddress("X");
            u.setPhoneNumber("00" + i);
            u.setAllowNotifications(true);
            u.setIsArchived(false);
            u.setEfficiency(1.0);
            userRepo.save(u);
        });
        userId = userRepo.findByEmail("u1@t.test").get().getId();

        // 4) Zaloguj się jako owner, pobierz token
        AuthRequest auth = AuthRequest.builder()
                .email("own@t.test")
                .password("Owner1234!")
                .build();

        String resp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ownerToken = objectMapper.readTree(resp).get("token").asText();
    }

    @Test
    void addTeamMember_andCount() throws Exception {
        NewTeamMemberRequest req = NewTeamMemberRequest.builder()
                .teamId(teamId)
                .userId(userId)
                .build();

        mockMvc.perform(post("/admin/team-members")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.team.id").value(teamId))
                .andExpect(jsonPath("$.user.id").value(userId));

        mockMvc.perform(get("/admin/team-members/{id}/count", teamId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void updateTeamMember_changeUser() throws Exception {
        // Dodaj ręcznie członkostwo
        TeamMember saved = new TeamMember();
        saved.setTeam(teamRepo.findById(teamId).get());
        saved.setUser(userRepo.findByEmail("u1@t.test").get());
        saved.setCreatedAt(LocalDateTime.now());
        saved = tmRepo.save(saved);

        Long tmId = saved.getId();
        Long newUserId = userRepo.findByEmail("u2@t.test").get().getId();

        UpdateTeamMemberRequest upd = UpdateTeamMemberRequest.builder()
                .userId(newUserId)
                .build();

        mockMvc.perform(patch("/admin/team-members/{id}", tmId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(newUserId));
    }

    @Test
    void deleteTeamMember_andRemoveUserFromTeam() throws Exception {
        // Dodaj ręcznie
        TeamMember toDelete = new TeamMember();
        toDelete.setTeam(teamRepo.findById(teamId).get());
        toDelete.setUser(userRepo.findByEmail("u1@t.test").get());
        toDelete.setCreatedAt(LocalDateTime.now());
        tmRepo.save(toDelete);

        mockMvc.perform(delete("/admin/team-members")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("userId", userRepo.findByEmail("u1@t.test").get().getId().toString())
                        .param("teamId", teamId.toString()))
                .andExpect(status().isNoContent());
    }
    @Test
    void getAllTeamMembers_returnsPage() throws Exception {
        // Dodajemy dwóch członków, by mieć co paginować
        IntStream.rangeClosed(1, 2).forEach(i -> {
            TeamMember tm = new TeamMember();
            tm.setTeam(teamRepo.findById(teamId).get());
            tm.setUser(userRepo.findByEmail("u" + i + "@t.test").get());
            tm.setCreatedAt(LocalDateTime.now());
            tmRepo.save(tm);
        });

        mockMvc.perform(get("/admin/team-members")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // co najmniej 2 rekordy w content
                .andExpect(jsonPath("$.content.length()", greaterThanOrEqualTo(2)))
                // upewniamy się, że w content są userzy u1 i u2
                .andExpect(jsonPath("$.content[*].user.email", hasItems("u1@t.test", "u2@t.test")))
                // oraz że co najmniej 2 jest w totalElements
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(2)));
    }

    @Test
    void getTeamMemberById_returnsOne() throws Exception {
        TeamMember tm = new TeamMember();
        tm.setTeam(teamRepo.findById(teamId).get());
        tm.setUser(userRepo.findByEmail("u1@t.test").get());
        tm.setCreatedAt(LocalDateTime.now());
        tm = tmRepo.save(tm);

        mockMvc.perform(get("/admin/team-members/{id}", tm.getId())
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tm.getId()))
                .andExpect(jsonPath("$.team.id").value(teamId))
                .andExpect(jsonPath("$.user.id").value(userRepo.findByEmail("u1@t.test").get().getId()));
    }

    @Test
    void getTeamMembersByTeamId_returnsUsersList() throws Exception {
        // Dodajemy 3 członków
        IntStream.rangeClosed(1, 3).forEach(i -> {
            TeamMember tm = new TeamMember();
            tm.setTeam(teamRepo.findById(teamId).get());
            tm.setUser(userRepo.findByEmail("u" + i + "@t.test").get());
            tm.setCreatedAt(LocalDateTime.now());
            tmRepo.save(tm);
        });

        mockMvc.perform(get("/admin/team-members/{id}/users", teamId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").isNumber());
    }
}
