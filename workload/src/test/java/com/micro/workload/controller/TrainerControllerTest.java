package com.micro.workload.controller;

import com.micro.workload.model.base.Trainer;
import com.micro.workload.service.impl.TrainerService;
import com.micro.workload.service.impl.TrainerStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrainerController.class)
@ExtendWith(SpringExtension.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @Test
    @DisplayName("GET /trainers/{username} - Success")
    void testGetTrainerSummary_WithValidUsername_ReturnsOkAndTrainer() throws Exception {
        Trainer mockTrainer = new Trainer();
        mockTrainer.setUsername("JohnDoe1");
        mockTrainer.setFirstName("John");
        mockTrainer.setLastName("Doe");
        mockTrainer.setStatus(true);

        given(trainerService.getTrainerSummary("JohnDoe1")).willReturn(mockTrainer);

        mockMvc.perform(get("/trainers/{username}", "JohnDoe1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("JohnDoe1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.status").value("true"));
    }

    @Test
    @DisplayName("GET /trainers - Success (Empty List)")
    void testGetAllTrainers_NoTrainers_ReturnsOkAndEmptyList() throws Exception {
        Collection<Trainer> emptyTrainers = Collections.emptyList();
        when(trainerService.getAllTrainers()).thenReturn(emptyTrainers);

        mockMvc.perform(get("/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /trainers - Success (Non-Empty List)")
    void testGetAllTrainers_WithTrainers_ReturnsOkAndTrainerList() throws Exception {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("alice");
        trainer1.setFirstName("Alice");
        trainer1.setLastName("Wonderland");

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("bob");
        trainer2.setFirstName("Bob");
        trainer2.setLastName("Builder");

        Collection<Trainer> trainerList = Arrays.asList(trainer1, trainer2);
        when(trainerService.getAllTrainers()).thenReturn(trainerList);

        mockMvc.perform(get("/trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].username").value("bob"))
                .andExpect(jsonPath("$[1].lastName").value("Builder"));
    }

    @Test
    @DisplayName("getTrainerSummary returns Trainer if found, otherwise throws exception")
    void testGetTrainerSummary() {
        TrainerStorageService trainerStorageService = Mockito.mock(TrainerStorageService.class);
        TrainerService trainerService = new TrainerService(trainerStorageService);

        Trainer expectedTrainer = new Trainer();
        expectedTrainer.setUsername("john");
        given(trainerStorageService.getTrainer("john"))
                .willReturn(Optional.of(expectedTrainer));

        Trainer actualTrainer = trainerService.getTrainerSummary("john");
        assertNotNull(actualTrainer);
        assertEquals("john", actualTrainer.getUsername());

        given(trainerStorageService.getTrainer("invalid"))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            trainerService.getTrainerSummary("invalid");
        });
    }

    @Test
    @DisplayName("getAllTrainers returns all trainer objects if present, otherwise empty list")
    void testGetAllTrainers() {
        TrainerStorageService trainerStorageService = Mockito.mock(TrainerStorageService.class);
        TrainerService trainerService = new TrainerService(trainerStorageService);

        Map<String, Trainer> mockMap = new ConcurrentHashMap<>();
        Trainer t1 = new Trainer();
        t1.setUsername("user1");
        Trainer t2 = new Trainer();
        t2.setUsername("user2");

        mockMap.put(t1.getUsername(), t1);
        mockMap.put(t2.getUsername(), t2);

        given(trainerStorageService.getAllTrainers())
                .willReturn(Optional.of(mockMap));

        assertEquals(2, trainerService.getAllTrainers().size());

        given(trainerStorageService.getAllTrainers())
                .willReturn(Optional.empty());

        assertTrue(trainerService.getAllTrainers().isEmpty());
    }

}
