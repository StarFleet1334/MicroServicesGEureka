package com.micro.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.WorkLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkLoadController.class)
class WorkLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkLoadService workLoadService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})
    void trainingAddedSuccessfully() throws Exception {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setTrainerUserName("jdoe");
        dto.setTrainerFirstName("John");
        dto.setTrainerLastName("Doe");
        dto.setIsActive(true);
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(120.0);

        doNothing().when(workLoadService).trainingAdded(any(TrainingSessionDTO.class));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(workLoadService).trainingAdded(any(TrainingSessionDTO.class));
    }


    @Test
    @WithMockUser(username="admin", roles={"USER", "ADMIN"})
    void trainingDeletedSuccessfully() throws Exception {
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setTrainerUserName("jdoe");
        dto.setTrainerFirstName("John");
        dto.setTrainerLastName("Doe");
        dto.setIsActive(true);
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(120.0);

        doNothing().when(workLoadService).trainingDeleted(any(TrainingSessionDTO.class));

        mockMvc.perform(delete("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(workLoadService).trainingDeleted(any(TrainingSessionDTO.class));
    }
}
