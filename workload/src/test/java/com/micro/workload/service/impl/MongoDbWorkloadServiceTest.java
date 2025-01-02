package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.repository.TrainerMongoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MongoDbWorkloadServiceTest {

    @MockBean
    private TrainerMongoRepository trainerMongoRepository;

    @Autowired
    private MongoDbWorkloadService mongoDbWorkloadService;

    @Test
    void testFindTrainerByUsername_TrainerExists() {
        String username = "validTrainer";
        Trainer trainer = new Trainer(username, "John", "Doe", true);
        when(trainerMongoRepository.findById(username))
                .thenReturn(Optional.of(trainer));

        Trainer result = mongoDbWorkloadService.findTrainerByUsername(username);

        assertEquals(trainer, result);
        verify(trainerMongoRepository, times(1)).findById(username);
    }

    @Test
    void testSaveTrainer() {
        Trainer trainer = new Trainer("newTrainer", "Alice", "Wonderland", true);
        when(trainerMongoRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mongoDbWorkloadService.saveTrainer(trainer);

        verify(trainerMongoRepository).save(trainer);
    }
}
