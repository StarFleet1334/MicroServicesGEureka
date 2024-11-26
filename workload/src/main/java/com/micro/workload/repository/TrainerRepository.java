package com.micro.workload.repository;

import com.micro.workload.model.Trainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrainerRepository {
    private static Map<String, Trainer> trainers = new ConcurrentHashMap<>();

    public static Trainer getTrainer(String username) {
        return trainers.get(username);
    }

    public static void addTrainer(Trainer trainer) {
        trainers.putIfAbsent(trainer.getUsername(), trainer);
    }

    public static void removeTrainer(String username) {
        trainers.remove(username);
    }

    public static Map<String, Trainer> getAllTrainers() {
        return trainers;
    }
}
