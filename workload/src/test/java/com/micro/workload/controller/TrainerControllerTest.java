package com.micro.workload.controller;

import com.micro.workload.model.Trainer;
import com.micro.workload.repository.TrainerRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainerControllerTest {

    @Autowired
    private TrainerController trainerController;

    @SpyBean
    private TrainerRepository trainerRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;


    @BeforeEach
    void setUp() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("trainerController");
        circuitBreaker.reset();
    }

    @Test
    void testGetTrainerSummaryCircuitBreaker() {
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("trainerController");
        AtomicBoolean fallbackCalled = new AtomicBoolean(false);
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> fallbackCalled.set(true));
        Mockito.doThrow(new RuntimeException("Simulated service failure"))
                .when(trainerRepository).getTrainer(Mockito.anyString());
        for (int i = 0; i < 15; i++) {
            try {
                trainerController.getTrainerSummary("testUser");
            } catch (Exception ignored) {
            }
        }

        Trainer result = trainerController.getTrainerSummary("testUser");
        assertTrue(fallbackCalled.get(), "Circuit Breaker did not prevent the call");
        assertNull(result, "Fallback method did not return null as expected");
    }

}
