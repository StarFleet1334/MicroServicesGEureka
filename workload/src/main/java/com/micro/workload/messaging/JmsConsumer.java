package com.micro.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.MongoDbWorkloadService;
import com.micro.workload.service.impl.WorkLoadService;
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
    private final MongoDbWorkloadService mongoDbWorkloadService;

    @Autowired
    public JmsConsumer(ObjectMapper objectMapper, WorkLoadService workLoadService, MongoDbWorkloadService mongoDbWorkloadService) {
        this.objectMapper = objectMapper;
        this.workLoadService = workLoadService;
        this.mongoDbWorkloadService = mongoDbWorkloadService;
    }

    @JmsListener(destination =  "${my.jms.queue-name}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message, @Header(name = "TransactionID", required = false) String transactionId) {
        try {
            TrainingSessionDTO dto = objectMapper.readValue(message, TrainingSessionDTO.class);
            LOGGER.info("Received message from Queue: {} with TransactionID: {}",  "${my.jms.queue-name}", transactionId);

            switch (dto.getAction()) {
                case "add":
                    LOGGER.info("Received training added event: {}", dto);
                    workLoadService.trainingAdded(dto);
                    mongoDbWorkloadService.trainingAdded(dto);
                    break;
                case "delete":
                    LOGGER.info("Received training deleted event: {}", dto);
                    workLoadService.trainingDeleted(dto);
                    mongoDbWorkloadService.trainingDeleted(dto);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action specified");
            }

        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", e.getMessage());
        }
    }
}
