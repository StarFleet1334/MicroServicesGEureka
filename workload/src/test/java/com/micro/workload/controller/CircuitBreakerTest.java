
package com.micro.workload.controller;

import com.micro.workload.repository.TrainerRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CircuitBreakerTest {

    @SpyBean
    private TrainerRepository trainerRepository;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Test
    void testGetTrainerCircuitBreaker() {
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("trainerRepository");
        AtomicBoolean fallbackCalled = new AtomicBoolean(false);
        circuitBreaker.getEventPublisher()
                .onCallNotPermitted(event -> fallbackCalled.set(true));

        Mockito.doThrow(new RuntimeException("Simulated service failure"))
                .when(trainerRepository).getTrainer(Mockito.anyString());
        for (int i = 0; i < 15; i++) {
            try {
                trainerRepository.getTrainer("testUser");
            } catch (RuntimeException ignored) {

            }
        }
        trainerRepository.getTrainer("testUser");
        assertTrue(fallbackCalled.get(), "Circuit Breaker did not prevent the call");
    }
}
