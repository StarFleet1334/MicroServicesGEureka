package com.micro.workload.service.base;

import com.micro.workload.model.dto.TrainingSessionDTO;

public interface BaseService {
    default void trainingAdded(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, true);
    };
    default void trainingDeleted(TrainingSessionDTO trainingSessionDTO) {
        updateTrainingSummary(trainingSessionDTO, false);
    };
    void updateTrainingSummary(TrainingSessionDTO dto, boolean isAddition);
}
