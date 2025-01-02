package com.micro.workload.service.impl;

import com.micro.workload.model.base.MonthSummary;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.base.YearSummary;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.base.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public abstract class AbstractWorkLoadService implements BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWorkLoadService.class);

    @Override
    public void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition) {
        Trainer trainer = findTrainerByUsername(dto.getTrainerUserName());

        if (trainer == null && isAddition) {
            trainer = new Trainer(
                    dto.getTrainerUserName(),
                    dto.getTrainerFirstName(),
                    dto.getTrainerLastName(),
                    dto.getActive()
            );
            saveTrainer(trainer);
        } else if (trainer == null && !isAddition) {
            LOGGER.warn("Trainer not found for username: {} when trying to delete training.", dto.getTrainerUserName());
            return;
        }

        updateTrainerSummaryInternal(trainer, dto, isAddition);
        saveTrainer(trainer);

        LOGGER.info(
                "Updated training summary for trainer {}: Year {}, Month {}, Duration {}",
                dto.getTrainerUserName(),
                dto.getTrainingDate().getYear(),
                dto.getTrainingDate().getMonthValue(),
                dto.getTrainingDuration()
        );
    }

    protected abstract Trainer findTrainerByUsername(String username);

    protected abstract void saveTrainer(Trainer trainer);

    private void updateTrainerSummaryInternal(Trainer trainer, TrainingSessionDTO dto, boolean isAddition) {
        LocalDate trainingDate = dto.getTrainingDate();
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        YearSummary yearSummary = trainer.getYearSummaries()
                .computeIfAbsent(year, YearSummary::new);

        MonthSummary monthSummary = yearSummary.getMonthSummaries()
                .computeIfAbsent(month, MonthSummary::new);

        int duration = dto.getTrainingDuration().intValue();

        if (isAddition) {
            monthSummary.addDuration(duration);
        } else {
            monthSummary.subtractDuration(duration);
        }
    }
}

