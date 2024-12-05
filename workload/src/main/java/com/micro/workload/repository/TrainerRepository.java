package com.micro.workload.repository;

import com.micro.workload.model.base.Trainer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrainerRepository {
    private static Map<String, Trainer> trainers = new ConcurrentHashMap<>();

    @CircuitBreaker(name = "trainerRepository", fallbackMethod = "fallbackGetTrainer")
    public Trainer getTrainer(String username) {
        return trainers.get(username);
    }

    @CircuitBreaker(name = "trainerRepository", fallbackMethod = "fallbackAddTrainer")
    public void addTrainer(Trainer trainer) {
        trainers.putIfAbsent(trainer.getUsername(), trainer);
    }

    public void removeTrainer(String username) {
        trainers.remove(username);
    }

    public Map<String, Trainer> getAllTrainers() {
        return trainers;
    }

    public Trainer fallbackGetTrainer(String username, Throwable throwable) {
        return null;
    }

    public void fallbackAddTrainer(Trainer trainer, Throwable throwable) {
    }

}
