package com.micro.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.controller.WorkLoadController;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.WorkLoadService;
import com.micro.workload.utils.ActiveMQConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class JmsConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConsumer.class);

    private final ObjectMapper objectMapper;
    private final WorkLoadService workLoadService;

    @Autowired
    public JmsConsumer(ObjectMapper objectMapper, WorkLoadService workLoadService) {
        this.objectMapper = objectMapper;
        this.workLoadService = workLoadService;
    }

    @JmsListener(destination = ActiveMQConstants.TRAININGS_QUEUE, containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message, @Header(name = "TransactionID", required = false) String transactionId) {
        try {
            TrainingSessionDTO dto = objectMapper.readValue(message, TrainingSessionDTO.class);
            LOGGER.info("Received message from Queue: {} with TransactionID: {}", ActiveMQConstants.TRAININGS_QUEUE, transactionId);

            switch (dto.getAction()) {
                case "add":
                    LOGGER.info("Received training added event: {}", dto);
                    workLoadService.trainingAdded(dto);
                    break;
                case "delete":
                    LOGGER.info("Received training deleted event: {}", dto);
                    workLoadService.trainingDeleted(dto);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action specified");
            }

        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage());
        }
    }
}