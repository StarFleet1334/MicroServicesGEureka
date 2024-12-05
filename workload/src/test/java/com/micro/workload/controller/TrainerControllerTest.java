package com.micro.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerRepository trainerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetTrainerSummary() throws Exception {
        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername("testUser");
        expectedTrainer.setFirstName("John");
        expectedTrainer.setLastName("Doe");

        doReturn(expectedTrainer).when(trainerRepository).getTrainer(anyString());

        mockMvc.perform(get("/trainers/testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedTrainer)));
    }

    @Test
    void testGetTrainerSummaryNotFound() throws Exception {
        doReturn(null).when(trainerRepository).getTrainer(anyString());

        mockMvc.perform(get("/trainers/testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
