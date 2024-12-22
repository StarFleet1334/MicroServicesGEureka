package com.micro.workload.service.impl;

import com.micro.workload.model.base.MonthSummary;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.base.YearSummary;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.repository.TrainerMongoRepository;
import com.micro.workload.service.base.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MongoDbWorkloadService implements BaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbWorkloadService.class);

    private final TrainerMongoRepository trainerMongoRepository;

    public MongoDbWorkloadService(TrainerMongoRepository trainerMongoRepository) {
        this.trainerMongoRepository = trainerMongoRepository;
    }

    @Override
    public void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition) {
        String username = dto.getTrainerUserName();

        Trainer trainer = trainerMongoRepository.findById(username).orElse(null);

        if (trainer == null) {
            if (!isAddition) {
                LOGGER.warn("Trainer not found for username: {} when trying to delete training.", username);
                return;
            }
            trainer = new Trainer(
                    username,
                    dto.getTrainerFirstName(),
                    dto.getTrainerLastName(),
                    dto.getActive()
            );
        }
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

        trainerMongoRepository.save(trainer);

        LOGGER.info("Updated training summary for trainer {}: Year {}, Month {}, Total Duration {}",
                username, year, month, monthSummary.getTotalDuration());
    }
}
