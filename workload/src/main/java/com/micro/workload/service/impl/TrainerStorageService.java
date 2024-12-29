package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerStorageService {
    private static Map<String, Trainer> trainers = new ConcurrentHashMap<>();

    @CircuitBreaker(name = "trainerRepository", fallbackMethod = "fallbackGetTrainer")
    public Optional<Trainer> getTrainer(String username) {
        return Optional.ofNullable(trainers.get(username));
    }

    @CircuitBreaker(name = "trainerRepository", fallbackMethod = "fallbackAddTrainer")
    public void addTrainer(Trainer trainer) {
        trainers.putIfAbsent(trainer.getUsername(), trainer);
    }

    public Optional<Map<String, Trainer>> getAllTrainers() {
        return Optional.ofNullable(trainers);
    }

}
