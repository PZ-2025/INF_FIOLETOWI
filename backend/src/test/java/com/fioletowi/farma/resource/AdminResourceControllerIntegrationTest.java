package com.fioletowi.farma.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fioletowi.farma.auth.AuthRequest;
import com.fioletowi.farma.task.TaskResource;          // potrzebne do czyszczenia zależnej tabeli
import com.fioletowi.farma.task.TaskResourceRepository;
import com.fioletowi.farma.user.User;
import com.fioletowi.farma.user.UserRepository;
import com.fioletowi.farma.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminResourceControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired ResourceRepository resourceRepository;
    @Autowired TaskResourceRepository taskResourceRepository;    // <<-- dodane
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;

    private String ownerToken;

    @BeforeEach
    void setUp() throws Exception {
        taskResourceRepository.deleteAll();
        resourceRepository.deleteAll();
        userRepository.deleteAll();

        // seed Owner
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

        // login owner
        var auth = AuthRequest.builder()
                .email("owner@t.test")
                .password("Pass1234!")
                .build();
        var resp = mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        ownerToken = objectMapper.readTree(resp).get("token").asText();
        assertThat(ownerToken).isNotBlank();
    }

    @Test
    void getAllResources() throws Exception {
        mockMvc.perform(get("/admin/resource")
                        .header("Authorization", "Bearer " + ownerToken)
                        .param("page", "0").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void createResource() throws Exception {
        var req = NewResourceRequest.builder()
                .name("Resource1")
                .quantity(BigDecimal.valueOf(10))
                .resourceType(ResourceType.FOR_SELL)
                .build();
        var body = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/admin/resource")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Resource1"))
                .andExpect(jsonPath("$.quantity").value(10))
                // poprawiony expected value: FOR_SELL zamiast MATERIAL
                .andExpect(jsonPath("$.resourceType").value("FOR_SELL"));
    }

    @Test
    void getResourceById() throws Exception {
        Resource resource = new Resource();
        resource.setName("Resource1");
        resource.setQuantity(BigDecimal.valueOf(10));
        resource.setResourceType(ResourceType.FOR_SELL);
        Long id = resourceRepository.save(resource).getId();

        mockMvc.perform(get("/admin/resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Resource1"));
    }

    @Test
    void partialUpdateResource() throws Exception {
        Resource resource = new Resource();
        resource.setName("Resource1");
        resource.setQuantity(BigDecimal.valueOf(10));
        resource.setResourceType(ResourceType.FOR_SELL);
        Long id = resourceRepository.save(resource).getId();

        var req = UpdateResourceRequest.builder()
                .name("UpdatedResource")
                .build();
        var body = objectMapper.writeValueAsString(req);

        mockMvc.perform(patch("/admin/resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedResource"));
    }

    @Test
    void deleteResource() throws Exception {
        Resource resource = new Resource();
        resource.setName("Resource1");
        resource.setQuantity(BigDecimal.valueOf(10));
        resource.setResourceType(ResourceType.FOR_SELL);
        Long id = resourceRepository.save(resource).getId();

        mockMvc.perform(delete("/admin/resource/{id}", id)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());

        assertThat(resourceRepository.existsById(id)).isFalse();
    }
}
