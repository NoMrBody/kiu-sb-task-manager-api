package org.example.todoapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TaskManagerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "alice")
    void createTask_thenGetById_returnsPersistedTask() throws Exception {
        String dueDate = LocalDate.now().toString();

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Integration test task",
                                  "description": "Created by TaskManagerIntegrationTest",
                                  "status": "TODO",
                                  "dueDate": "%s",
                                  "userId": 2
                                }
                                """.formatted(dueDate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").exists())
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.ownerUsername").value("alice"))
                .andReturn();

        Number taskId = JsonPath.read(
                createResult.getResponse().getContentAsString(),
                "$.taskId"
        );

        mockMvc.perform(get("/api/tasks/{id}", taskId.longValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId.longValue()))
                .andExpect(jsonPath("$.title").value("Integration test task"))
                .andExpect(jsonPath("$.description").value("Created by TaskManagerIntegrationTest"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.ownerUsername").value("alice"));
    }
}
