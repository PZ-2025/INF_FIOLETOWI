package com.fioletowi.farma.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put; // <— import PUT
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String workerToken;
    private Long workerId;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Utwórz jednego pracownika
        User worker = User.builder()
                .firstName("Self")
                .lastName("Worker")
                .email("self@example.com")
                .password(passwordEncoder.encode("SelfPass1!"))
                .birthDate(LocalDateTime.of(1992,2,2,0,0))
                .hiredAt(LocalDateTime.now())
                .status(null)
                .note(null)
                .efficiency(1.0)
                .userRole(UserRole.WORKER)
                .address("Town")
                .phoneNumber("+48100100100")
                .allowNotifications(false)
                .isArchived(false)
                .build();
        worker = userRepository.save(worker);
        workerId = worker.getId();

        // 2) Zaloguj się jako ten worker i pobierz JWT
        AuthRequest auth = AuthRequest.builder()
                .email("self@example.com")
                .password("SelfPass1!")
                .build();
        String authJson = objectMapper.writeValueAsString(auth);

        String resp = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .post("/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(authJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(resp);
        workerToken = node.get("token").asText();
    }

    @Test
    void getCurrentUser() throws Exception {
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer " + workerToken)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("self@example.com"))
                .andExpect(jsonPath("$.id").value(workerId));
    }

    @Test
    void partialUpdateSelf() throws Exception {
        UpdateUserRequest req = UpdateUserRequest.builder()
                .firstName("Updated")
                .address("NewAddress")
                .build();
        String body = objectMapper.writeValueAsString(req);

        mockMvc.perform(patch("/user/{id}", workerId)
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.address").value("NewAddress"));
    }

    @Test
    void updateSettings() throws Exception {
        UserSettingsRequest settings = new UserSettingsRequest(true);
        String body = objectMapper.writeValueAsString(settings);

        mockMvc.perform(put("/user/settings")
                        .header("Authorization", "Bearer " + workerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowNotifications").value(true));
    }
}
