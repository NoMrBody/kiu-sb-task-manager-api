package org.example.todoapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ActuatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint_isPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void infoEndpoint_requiresAdmin() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void infoEndpoint_returnsConfiguredMetadata_asAdmin() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Task Manager API"))
                .andExpect(jsonPath("$.app.version").value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.app.contact").value("admin@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void metricsEndpoint_requiresAdmin() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void metricsEndpoint_isAvailable_asAdmin() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }
}
