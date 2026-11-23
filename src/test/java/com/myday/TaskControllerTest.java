package com.myday;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void addTask_shouldCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Acheter du pain\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Acheter du pain")))
            .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void addTask_withBlankTitle_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"  \"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_shouldReturnList() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Tache 1\"}"));
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Tache 2\"}"));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void toggleTask_shouldMarkAsCompleted() throws Exception {
        Task task = new Task();
        task.setTitle("A terminer");
        Task saved = repository.save(task);

        mockMvc.perform(patch("/api/tasks/" + saved.getId() + "/toggle"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void toggleTask_unknownId_shouldReturn404() throws Exception {
        mockMvc.perform(patch("/api/tasks/9999/toggle"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldRemoveTask() throws Exception {
        Task task = new Task();
        task.setTitle("A supprimer");
        Task saved = repository.save(task);

        mockMvc.perform(delete("/api/tasks/" + saved.getId()))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks"))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteTask_unknownId_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/tasks/9999"))
            .andExpect(status().isNotFound());
    }
}