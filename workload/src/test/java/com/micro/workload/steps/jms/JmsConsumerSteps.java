package com.micro.workload.steps.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.messaging.JmsConsumer;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.MongoDbWorkloadService;
import com.micro.workload.service.impl.WorkLoadService;
import com.micro.workload.utils.ActiveMQConstants;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import io.cucumber.java.en.*;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@CucumberContextConfiguration
@SpringBootTest
public class JmsConsumerSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConsumerSteps.class);

    @Mock
    private WorkLoadService workLoadService;

    @Mock
    private MongoDbWorkloadService mongoDbWorkloadService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private JmsConsumer jmsConsumer;

    private TrainingSessionDTO trainingSessionDTO;
    private String message;

    @Given("the ActiveMQ queue is ready to receive messages")
    public void theActiveMQQueueIsReadyToReceiveMessages() {

    }

    @Given("a valid {string} training message is sent to the queue")
    public void a_valid_training_message_is_sent_to_the_queue(String action) throws Exception {
        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName("trainer123");
        trainingSessionDTO.setTrainerFirstName("John");
        trainingSessionDTO.setTrainerLastName("Doe");
        trainingSessionDTO.setActive(true);
        trainingSessionDTO.setTrainingDate(LocalDate.now());
        trainingSessionDTO.setTrainingDuration(3);
        trainingSessionDTO.setAction(action);
        message = objectMapper.writeValueAsString(trainingSessionDTO);
        jmsTemplate.convertAndSend(ActiveMQConstants.TRAININGS_QUEUE, message);
    }

    @When("the JmsConsumer processes the message")
    public void the_JmsConsumer_processes_the_message() {
        jmsConsumer.onMessage(message, "TX12345");
    }

    @Then("the training is added to both workload services")
    public void the_training_is_added_to_both_workload_services() {
        Assertions.assertEquals("add", trainingSessionDTO.getAction());
        Mockito.doNothing().when(workLoadService).trainingAdded(any());
        // I set ... since it is not required for now
        jmsConsumer.onMessage(message, "...");
    }

    @Then("the training is deleted from both workload services")
    public void the_training_is_deleted_from_both_workload_services() {
        Assertions.assertEquals("delete", trainingSessionDTO.getAction());
        Mockito.when(workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName())).thenReturn(null);

        jmsConsumer.onMessage(message, "...");
    }

    @Then("the logs contain {string}")
    public void the_logs_contain(String logMessage) {
        LOGGER.info("Verify logs contain: {}", logMessage);
    }

    @Given("an invalid action type message is sent to the queue")
    public void an_invalid_action_type_message_is_sent_to_the_queue() throws Exception {
        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName("trainer123");
        trainingSessionDTO.setTrainerFirstName("John");
        trainingSessionDTO.setTrainerLastName("Doe");
        trainingSessionDTO.setActive(true);
        trainingSessionDTO.setTrainingDate(LocalDate.now());
        trainingSessionDTO.setTrainingDuration(3);
        trainingSessionDTO.setAction("invalidAction");
        message = objectMapper.writeValueAsString(trainingSessionDTO);
        jmsTemplate.convertAndSend(ActiveMQConstants.TRAININGS_QUEUE, message);
    }

    @Then("an {string} error is logged")
    public void an_error_is_logged(String errorMessage) {
        LOGGER.error("Verify error logs contain: {}", errorMessage);
    }

    @Given("a message with invalid format is sent to the queue")
    public void a_message_with_invalid_format_is_sent_to_the_queue() {
        message = "INVALID_MESSAGE_FORMAT";
        jmsTemplate.convertAndSend(ActiveMQConstants.TRAININGS_QUEUE, message);
    }

    @Then("no actions are performed on the workload services")
    public void no_actions_are_performed_on_the_workload_services() {
        verifyNoInteractions(workLoadService);
        verifyNoInteractions(mongoDbWorkloadService);
    }
}
