package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.repository.TrainerMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerStorageService trainerStorageService;
    private final TrainerMongoRepository trainerMongoRepository;

    public TrainerService(TrainerStorageService trainerStorageService, TrainerMongoRepository trainerMongoRepository) {
        this.trainerStorageService = trainerStorageService;
        this.trainerMongoRepository = trainerMongoRepository;
    }

    public Trainer getTrainerSummary(String username) {
        return trainerStorageService.getTrainer(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found for username: " + username));
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerStorageService.getAllTrainers()
                .map(Map::values)
                .orElseGet(Collections::emptyList);
    }

    public List<Trainer> getTrainersByName(String firstName, String lastName) {
        return trainerMongoRepository.findByFirstNameAndLastName(firstName, lastName);
    }

}
