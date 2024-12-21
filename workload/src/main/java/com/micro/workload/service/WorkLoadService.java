package com.micro.workload.service;

import com.micro.workload.model.base.MonthSummary;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.model.base.YearSummary;
import com.micro.workload.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WorkLoadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkLoadService.class);

    private final TrainerRepository trainerRepository;

    public WorkLoadService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public void trainingAdded(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, true);
    }

    public void trainingDeleted(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, false);
    }

    private void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition) {
        String username = dto.getTrainerUserName();
        Trainer trainer = trainerRepository.getTrainer(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found for username: " + username));

        if (trainer == null) {
            if (isAddition) {
                trainer = new Trainer(username, dto.getTrainerFirstName(), dto.getTrainerLastName(), dto.getActive());
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
