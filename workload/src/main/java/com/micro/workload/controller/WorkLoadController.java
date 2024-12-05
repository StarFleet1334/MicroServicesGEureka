package com.micro.workload.controller;

import com.micro.workload.model.*;
import com.micro.workload.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/trainings")
public class WorkLoadController {

    @Autowired
    private TrainerRepository trainerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLoadController.class);

    @PostMapping
    public void trainingAdded(@RequestBody TrainingSessionDTO trainingSessionDTO) {
        LOGGER.info("Received training added event: {}", trainingSessionDTO);
        updateTrainingSummary(trainingSessionDTO, true);
    }

    @DeleteMapping
    public void trainingDeleted(@RequestBody TrainingSessionDTO trainingSessionDTO) {
        LOGGER.info("Received training deleted event: {}", trainingSessionDTO);
        updateTrainingSummary(trainingSessionDTO, false);
    }

    private void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition) {
        String username = dto.getTrainerUserName();
        Trainer trainer = trainerRepository.getTrainer(username);

        if (trainer == null) {
            if (isAddition) {
                trainer = new Trainer(username, dto.getTrainerFirstName(), dto.getTrainerLastName(), dto.getIsActive());
                trainerRepository.addTrainer(trainer);
            } else {
                return;
            }
        }
        LocalDate trainingDate = dto.getTrainingDate();
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        YearSummary yearSummary = trainer.getYearSummaries().computeIfAbsent(year, YearSummary::new);

        MonthSummary monthSummary = yearSummary.getMonthSummaries().computeIfAbsent(month, MonthSummary::new);


        int duration = dto.getTrainingDuration().intValue();

        if (isAddition) {
            monthSummary.addDuration(duration);
        } else {
            monthSummary.subtractDuration(duration);
        }

        LOGGER.info("Updated training summary for trainer {}: Year {}, Month {}, Total Duration {}",
                username, year, month, monthSummary.getTotalDuration());
    }
}
