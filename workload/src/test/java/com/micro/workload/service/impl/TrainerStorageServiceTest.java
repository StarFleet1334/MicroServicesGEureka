package com.micro.workload.service.impl;

import com.micro.workload.model.base.Trainer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainerStorageServiceTest {

    private TrainerStorageService trainerStorageService;

    @BeforeEach
    void setUp() {
        trainerStorageService = new TrainerStorageService();
        clearStaticMap(); // ensure a fresh map for each test
    }

    /**
     * Clear the static 'trainers' map via reflection so we start each test with an empty map.
     * This is needed because 'trainers' is static and not exposed via any method.
     */
    private void clearStaticMap() {
        try {
            Field trainersField = TrainerStorageService.class.getDeclaredField("trainers");
            trainersField.setAccessible(true);
            Map<String, Trainer> trainersMap = (Map<String, Trainer>) trainersField.get(null);
            trainersMap.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("getTrainer should return empty if no trainer found")
    void testGetTrainer_NotFound() {
        Optional<Trainer> result = trainerStorageService.getTrainer("unknown");
        assertTrue(result.isEmpty(), "Should return empty when trainer does not exist");
    }

    @Test
    @DisplayName("addTrainer should store the trainer in the map, and getTrainer should retrieve it")
    void testAddTrainer() {
        Trainer trainer = new Trainer("alice", "Alice", "Wonderland", true);

        trainerStorageService.addTrainer(trainer);
        Optional<Trainer> result = trainerStorageService.getTrainer("alice");

        assertTrue(result.isPresent(), "Trainer should be present after adding");
        assertEquals("alice", result.get().getUsername());
        assertEquals("Alice", result.get().getFirstName());
        assertEquals("Wonderland", result.get().getLastName());
        assertTrue(result.get().isStatus());
    }

    @Test
    @DisplayName("addTrainer should not override existing trainer if username is the same")
    void testAddTrainer_DoesNotOverride() {
        Trainer trainer1 = new Trainer("bob", "Bob", "First", true);
        Trainer trainer2 = new Trainer("bob", "Bob", "Second", false);

        trainerStorageService.addTrainer(trainer1);
        trainerStorageService.addTrainer(trainer2);

        Optional<Trainer> result = trainerStorageService.getTrainer("bob");
        assertTrue(result.isPresent());
        assertEquals("First", result.get().getLastName(), "Should keep the original trainer");
        assertTrue(result.get().isStatus(), "Should keep the original state (true)");
    }

    @Test
    @DisplayName("getAllTrainers should return an empty map initially")
    void testGetAllTrainers_Empty() {
        Optional<Map<String, Trainer>> allTrainers = trainerStorageService.getAllTrainers();
        assertTrue(allTrainers.isPresent(), "Optional should be present");
        assertTrue(allTrainers.get().isEmpty(), "Map should be empty at the start");
    }

    @Test
    @DisplayName("getAllTrainers should return all trainers added so far")
    void testGetAllTrainers_NonEmpty() {
        Trainer t1 = new Trainer("bob", "Bob", "Marley", true);
        Trainer t2 = new Trainer("charlie", "Charlie", "Brown", true);

        trainerStorageService.addTrainer(t1);
        trainerStorageService.addTrainer(t2);

        Optional<Map<String, Trainer>> allTrainers = trainerStorageService.getAllTrainers();
        assertTrue(allTrainers.isPresent(), "Optional should not be empty");

        Map<String, Trainer> map = allTrainers.get();
        assertEquals(2, map.size(), "Should have 2 trainers");
        assertTrue(map.containsKey("bob"), "Map should contain 'bob'");
        assertTrue(map.containsKey("charlie"), "Map should contain 'charlie'");
    }
}
