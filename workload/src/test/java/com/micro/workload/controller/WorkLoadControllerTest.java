package com.micro.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.TrainerStorageService;
import com.micro.workload.service.impl.WorkLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkLoadController.class)
@AutoConfigureMockMvc(addFilters = false)
class WorkLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private WorkLoadService workLoadService;
    private TrainerStorageService trainerStorageService;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        trainerStorageService = Mockito.mock(TrainerStorageService.class);
        workLoadService = new WorkLoadService(trainerStorageService);
    }

    @Test
    void testHandleTraining_missingTransactionId() throws Exception {
        TrainingSessionDTO request = new TrainingSessionDTO(
                "trainer123", "John", "Doe", true, LocalDate.now(), 2, "add"
        );
        performPost(request, null).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("findTrainerByUsername returns the found trainer")
    void testFindTrainerByUsername_found() {
        Trainer t = new Trainer("john", "John", "Doe", true);
        when(trainerStorageService.getTrainer("john")).thenReturn(Optional.of(t));

        Trainer result = workLoadService.findTrainerByUsername("john");
        assertEquals(t, result, "Should return the existing trainer");
    }

    @Test
    @DisplayName("saveTrainer calls trainerStorageService.addTrainer(...)")
    void testSaveTrainer() {
        Trainer t = new Trainer("bob", "Bob", "Builder", true);
        doNothing().when(trainerStorageService).addTrainer(any(Trainer.class));

        workLoadService.saveTrainer(t);
        verify(trainerStorageService, times(1)).addTrainer(t);
    }

    private ResultActions performPost(TrainingSessionDTO request, String transactionId) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/trainings")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        if (transactionId != null) {
            builder.header("Transaction-ID", transactionId);
        }

        return mockMvc.perform(builder);
    }


}