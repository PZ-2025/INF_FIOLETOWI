package com.fioletowi.farma.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testEmail = "test@example.com";
    private final String testPassword = "Test1234!";

    @BeforeEach
    void setUp() {

        User user = User.builder()
                .firstName("Test")
                .lastName("Logowania")
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .birthDate(LocalDateTime.of(1990, 1, 1, 0, 0))
                .hiredAt(LocalDateTime.now())
                .status(null)
                .note(null)
                .efficiency(1.0)
                .userRole(UserRole.WORKER)
                .address("Rzeszow")
                .phoneNumber("121212121")
                .allowNotifications(true)
                .isArchived(false)
                .build();

        userRepository.save(user);
        System.out.println(userRepository.findById(6L).orElseThrow().toString());
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() throws Exception {
        AuthRequest request = AuthRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .build();

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

}
