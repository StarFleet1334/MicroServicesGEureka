package com.micro.workload.service.base;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.dto.TrainingSessionDTO;

public interface BaseService {
    Trainer NO_TRAINER = new Trainer("noUser", "Unknown", "Unknown", false);

    default void trainingAdded(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, true);
    };
    default void trainingDeleted(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, false);
    };
    void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition);
}
