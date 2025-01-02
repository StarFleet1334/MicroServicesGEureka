package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.repository.TrainerMongoRepository;
import org.springframework.stereotype.Service;

@Service
public class MongoDbWorkloadService extends AbstractWorkLoadService {
    private final TrainerMongoRepository trainerMongoRepository;

    public MongoDbWorkloadService(TrainerMongoRepository trainerMongoRepository) {
        this.trainerMongoRepository = trainerMongoRepository;
    }

    @Override
    public Trainer findTrainerByUsername(String username) {
        return trainerMongoRepository.findById(username).orElse(NO_TRAINER);
    }

    @Override
    public void saveTrainer(Trainer trainer) {
        trainerMongoRepository.save(trainer);
    }
}

