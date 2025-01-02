package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import org.springframework.stereotype.Service;

@Service
public class WorkLoadService extends AbstractWorkLoadService {
    private final TrainerStorageService trainerStorageService;

    public WorkLoadService(TrainerStorageService trainerStorageService) {
        this.trainerStorageService = trainerStorageService;
    }

    @Override
    public Trainer findTrainerByUsername(String username) {
        return trainerStorageService.getTrainer(username).orElse(NO_TRAINER);
    }

    @Override
    public void saveTrainer(Trainer trainer) {
        trainerStorageService.addTrainer(trainer);
    }
}

