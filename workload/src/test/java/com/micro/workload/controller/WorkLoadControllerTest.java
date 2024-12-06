package com.micro.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.WorkLoadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkLoadController.class)
@AutoConfigureMockMvc
public class WorkLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkLoadService workLoadService;

    @Autowired
    private ObjectMapper objectMapper;

    @WithMockUser(username="ADMIN", roles={"USER","ADMIN"})
    @Test
    public void testHandleTraining_ValidAddAction() throws Exception {
        TrainingSessionDTO trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName("john_doe");
        trainingSessionDTO.setTrainerFirstName("John");
        trainingSessionDTO.setTrainerLastName("Doe");
        trainingSessionDTO.setIsActive(true);
        trainingSessionDTO.setTrainingDate(LocalDate.now());
        trainingSessionDTO.setTrainingDuration(2);
        trainingSessionDTO.setAction("add");

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTO)))
                .andExpect(status().isOk());

        Mockito.verify(workLoadService).trainingAdded(Mockito.any(TrainingSessionDTO.class));
    }

    @WithMockUser(username="ADMIN", roles={"USER","ADMIN"})
    @Test
    public void testHandleTraining_ValidDeleteAction() throws Exception {
        TrainingSessionDTO trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName("jane_doe");
        trainingSessionDTO.setTrainerFirstName("Jane");
        trainingSessionDTO.setTrainerLastName("Doe");
        trainingSessionDTO.setIsActive(false);
        trainingSessionDTO.setTrainingDate(LocalDate.now().minusDays(1));
        trainingSessionDTO.setTrainingDuration(1);
        trainingSessionDTO.setAction("delete");

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingSessionDTO)))
                .andExpect(status().isOk());

        Mockito.verify(workLoadService).trainingDeleted(Mockito.any(TrainingSessionDTO.class));
    }


}