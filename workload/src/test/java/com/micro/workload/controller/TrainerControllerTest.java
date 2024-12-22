package com.micro.workload.controller;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.service.impl.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @Test
    void testGetTrainerSummary_TrainerExists() throws Exception {
        Trainer trainer = new Trainer("john_doe", "John", "Doe", true);
        Mockito.when(trainerService.getTrainerSummary("john_doe")).thenReturn(trainer);

        mockMvc.perform(MockMvcRequestBuilders.get("/trainers/john_doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("john_doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetTrainerSummary_Trainer() throws Exception {
        Mockito.when(trainerService.getTrainerSummary("jane_doe")).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/trainers/jane_doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}