package com.micro.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.MongoDbWorkloadService;
import com.micro.workload.service.impl.WorkLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class JmsConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WorkLoadService workLoadService;

    @Mock
    private MongoDbWorkloadService mongoDbWorkloadService;

    @InjectMocks
    private JmsConsumer jmsConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnMessage_AddAction() throws Exception {
        String transactionId = "12345";
        String message = "{\"action\": \"add\", \"data\": \"test\"}";
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setAction("add");

        when(objectMapper.readValue(message, TrainingSessionDTO.class)).thenReturn(dto);

        jmsConsumer.onMessage(message, transactionId);

        verify(workLoadService, times(1)).trainingAdded(dto);
        verify(mongoDbWorkloadService, times(1)).trainingAdded(dto);
    }

    @Test
    void testOnMessage_DeleteAction() throws Exception {
        String transactionId = "67890";
        String message = "{\"action\": \"delete\", \"data\": \"test\"}";
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setAction("delete");

        when(objectMapper.readValue(message, TrainingSessionDTO.class)).thenReturn(dto);

        jmsConsumer.onMessage(message, transactionId);

        verify(workLoadService, times(1)).trainingDeleted(dto);
        verify(mongoDbWorkloadService, times(1)).trainingDeleted(dto);
    }

    @Test
    void testOnMessage_InvalidAction() throws Exception {
        String transactionId = "11111";
        String message = "{\"action\": \"invalid\", \"data\": \"test\"}";
        TrainingSessionDTO dto = new TrainingSessionDTO();
        dto.setAction("invalid");

        when(objectMapper.readValue(message, TrainingSessionDTO.class)).thenReturn(dto);

        jmsConsumer.onMessage(message, transactionId);

        verify(workLoadService, never()).trainingAdded(dto);
        verify(mongoDbWorkloadService, never()).trainingAdded(dto);
        verify(workLoadService, never()).trainingDeleted(dto);
        verify(mongoDbWorkloadService, never()).trainingDeleted(dto);
    }

    @Test
    void testOnMessage_ExceptionHandling() throws Exception {
        String transactionId = "22222";
        String message = "{\"action\": \"add\", \"data\": \"test\"}";

        when(objectMapper.readValue(message, TrainingSessionDTO.class)).thenThrow(new RuntimeException("JSON parse error"));

        jmsConsumer.onMessage(message, transactionId);

        verify(workLoadService, never()).trainingAdded(any());
        verify(mongoDbWorkloadService, never()).trainingAdded(any());
    }
}