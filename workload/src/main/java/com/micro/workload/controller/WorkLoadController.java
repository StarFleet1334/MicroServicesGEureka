package com.micro.workload.controller;

import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.WorkLoadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
public class WorkLoadController {

    @Autowired
    private WorkLoadService workLoadService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLoadController.class);


    @PostMapping
    public void handleTraining(@RequestBody TrainingSessionDTO trainingSessionDTO,
                               @RequestHeader("Transaction-ID") String transactionId) {
        LOGGER.info("Received training event with Transaction ID: {} - {}", transactionId, trainingSessionDTO);
        switch (trainingSessionDTO.getAction()) {
            case "add":
                LOGGER.info("Received training added event: {}", trainingSessionDTO);
                workLoadService.trainingAdded(trainingSessionDTO);
                break;
            case "delete":
                LOGGER.info("Received training deleted event: {}", trainingSessionDTO);
                workLoadService.trainingDeleted(trainingSessionDTO);
                break;
            default:
                LOGGER.error("Invalid action specified: {}", trainingSessionDTO.getAction());
                throw new IllegalArgumentException("Unknown action: " + trainingSessionDTO.getAction());

        }
    }

    public void setWorkLoadService(WorkLoadService workLoadService) {
        this.workLoadService = workLoadService;
    }
}