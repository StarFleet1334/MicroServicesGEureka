package com.micro.workload.controller;

import com.micro.workload.model.Trainer;
import com.micro.workload.repository.TrainerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private static Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    @Autowired
    private TrainerRepository trainerRepository;

    @CircuitBreaker(name = "trainerController", fallbackMethod = "fallbackGetTrainerSummary")
    @GetMapping("/{username}")
    public Trainer getTrainerSummary(@PathVariable String username) {
        Trainer trainer = trainerRepository.getTrainer(username);
        if (trainer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found");
        }
        return trainer;
    }

    @CircuitBreaker(name = "trainerController", fallbackMethod = "fallbackGetAllTrainers")
    @GetMapping
    public Collection<Trainer> getAllTrainers() {
        return TrainerRepository.getAllTrainers().values();
    }

    public Trainer fallbackGetTrainerSummary(String username, Throwable throwable) {
        LOGGER.info("Invoking fallback method for getTrainerSummary for username:");
        return null;
    }

    public Collection<Trainer> fallbackGetAllTrainers(Throwable throwable) {
        LOGGER.info("Invoking fallback method for getAllTrainers");
        return Collections.emptyList();
    }
}

