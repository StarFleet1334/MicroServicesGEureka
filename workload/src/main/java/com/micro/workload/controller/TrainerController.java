package com.micro.workload.controller;

import com.micro.workload.model.Trainer;
import com.micro.workload.repository.TrainerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping("/trainers")
public class TrainerController {

    @GetMapping("/{username}")
    public Trainer getTrainerSummary(@PathVariable String username) {
        Trainer trainer = TrainerRepository.getTrainer(username);
        if (trainer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found");
        }
        return trainer;
    }

    @GetMapping
    public Collection<Trainer> getAllTrainers() {
        return TrainerRepository.getAllTrainers().values();
    }
}

