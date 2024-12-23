package com.micro.workload.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.WorkLoadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkLoadController.class)
@AutoConfigureMockMvc(addFilters = false)
class WorkLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkLoadService workLoadService;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private ResultActions performPost(TrainingSessionDTO request, String transactionId) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/trainings")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        if (transactionId != null) {
            builder.header("Transaction-ID", transactionId);
        }

        return mockMvc.perform(builder);
    }

    @Test
    void testHandleTraining_addActionSuccess() throws Exception {
        TrainingSessionDTO request = new TrainingSessionDTO(
                "trainer123", "John", "Doe", true, LocalDate.now(), 2, "add"
        );
        doNothing().when(workLoadService).trainingAdded(Mockito.any(TrainingSessionDTO.class));

        performPost(request, "123456").andExpect(status().isOk());
    }

    @Test
    void testHandleTraining_deleteActionSuccess() throws Exception {
        TrainingSessionDTO request = new TrainingSessionDTO(
                "trainer123", "John", "Doe", true, LocalDate.now(), 2, "delete"
        );
        doNothing().when(workLoadService).trainingDeleted(Mockito.any(TrainingSessionDTO.class));

        performPost(request, "123456").andExpect(status().isOk());
    }

    @Test
    void testHandleTraining_missingTransactionId() throws Exception {
        TrainingSessionDTO request = new TrainingSessionDTO(
                "trainer123", "John", "Doe", true, LocalDate.now(), 2, "add"
        );
        performPost(request, null).andExpect(status().isBadRequest());
    }

}