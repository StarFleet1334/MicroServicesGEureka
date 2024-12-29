package com.micro.workload.service.impl;

import com.micro.workload.model.base.MonthSummary;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.model.base.YearSummary;
import com.micro.workload.service.base.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WorkLoadService implements BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLoadService.class);

    private final TrainerStorageService trainerStorageService;

    public WorkLoadService(TrainerStorageService trainerStorageService) {
        this.trainerStorageService = trainerStorageService;
    }

    public void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition) {
        String username = dto.getTrainerUserName();
        Trainer trainer = trainerStorageService.getTrainer(username)
                .orElse(null);

        if (trainer == null) {
            if (isAddition) {
                trainer = new Trainer(username, dto.getTrainerFirstName(), dto.getTrainerLastName(), dto.getActive());
                trainerStorageService.addTrainer(trainer);
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
