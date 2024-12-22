package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
public class TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;

    public TrainerService(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public Trainer getTrainerSummary(String username) {
        return trainerRepository.getTrainer(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found for username: " + username));
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerRepository.getAllTrainers()
                .map(Map::values)
                .orElseGet(Collections::emptyList);
    }

}
