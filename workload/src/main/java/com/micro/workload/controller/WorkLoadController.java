package com.micro.workload.controller;

import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.WorkLoadService;
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
    public void trainingAdded(@RequestBody TrainingSessionDTO trainingSessionDTO) {
        LOGGER.info("Received training added event: {}", trainingSessionDTO);
        workLoadService.trainingAdded(trainingSessionDTO);
    }

    @DeleteMapping
    public void trainingDeleted(@RequestBody TrainingSessionDTO trainingSessionDTO) {
        LOGGER.info("Received training deleted event: {}", trainingSessionDTO);
        workLoadService.trainingDeleted(trainingSessionDTO);
    }


}
