package com.micro.workload.controller;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.service.impl.TrainerService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private static Logger LOGGER = LoggerFactory.getLogger(TrainerController.class);

    @Autowired
    private TrainerService trainerService;

    @CircuitBreaker(name = "trainerController", fallbackMethod = "fallbackGetTrainerSummary")
    @GetMapping("/{username}")
    public Trainer getTrainerSummary(@PathVariable String username) {
        Trainer trainer = trainerService.getTrainerSummary(username);
        if (trainer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found");
        }
        return trainer;
    }

    @CircuitBreaker(name = "trainerController", fallbackMethod = "fallbackGetAllTrainers")
    @GetMapping
    public Collection<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    @GetMapping("/search")
    public List<Trainer> searchByName(
            @RequestParam String firstName,
            @RequestParam(required = false) String lastName
    ) {
        return trainerService.getTrainersByName(firstName, lastName);
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

