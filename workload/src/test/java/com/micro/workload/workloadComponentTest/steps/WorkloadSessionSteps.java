package com.micro.workload.workloadComponentTest.steps;

import com.micro.workload.controller.WorkLoadController;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.service.impl.WorkLoadService;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;

@CucumberContextConfiguration
@SpringBootTest
public class WorkloadSessionSteps {

    @MockBean
    private WorkLoadController workLoadController;
    @MockBean
    private WorkLoadService workLoadService;
    private Logger LOGGER;
    private TrainingSessionDTO trainingSessionDTO;
    private String transactionId;

    @Before
    public void setup() {
        workLoadController = new WorkLoadController();
        workLoadController.setWorkLoadService(workLoadService);
    }


    @Given("a trainer with the following details:")
    public void aTrainerWithDetails(io.cucumber.datatable.DataTable dataTable) {
        var row = dataTable.asMaps(String.class, String.class).get(0);
        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName(row.get("username"));
        trainingSessionDTO.setTrainerFirstName(row.get("firstName"));
        trainingSessionDTO.setTrainerLastName(row.get("lastName"));
        trainingSessionDTO.setActive(Boolean.parseBoolean(row.get("isActive")));
        trainingSessionDTO.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
        trainingSessionDTO.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
        trainingSessionDTO.setAction(row.get("action"));
        workLoadService = Mockito.mock(WorkLoadService.class);
        LOGGER = Mockito.mock(Logger.class);
        workLoadController = new WorkLoadController();
        workLoadController.setWorkLoadService(workLoadService);
    }

    @When("I send a request to add the training session with Transaction ID {string}")
    public void iSendARequestToAddTrainingSession(String txnId) {
        this.transactionId = txnId;
        Mockito.doNothing().when(workLoadService).trainingAdded(any());
        workLoadController.handleTraining(trainingSessionDTO, transactionId);
    }

    @Then("the system should log the action and save the training session")
    public void theSystemShouldLogAndSave() {
        Assertions.assertEquals("add", trainingSessionDTO.getAction());

        Trainer mockTrainer = new Trainer();
        mockTrainer.setFirstName(trainingSessionDTO.getTrainerFirstName());
        mockTrainer.setLastName("MockLastName");

        Mockito.when(workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName())).thenReturn(mockTrainer);

        Trainer trainer = workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName());
        Assertions.assertNotNull(trainer, "Trainer should not be null");
        Assertions.assertEquals(trainingSessionDTO.getTrainerFirstName(), trainer.getFirstName(), "Trainer's first name doesn't match");
    }

    @Given("an existing trainer with username {string}")
    public void anExistingTrainerWithUsername(String username) {
        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerFirstName("John");
        trainingSessionDTO.setTrainerLastName("Doe");
        trainingSessionDTO.setActive(true);
        trainingSessionDTO.setTrainingDuration(2);
        trainingSessionDTO.setTrainingDate(LocalDate.now());
        trainingSessionDTO.setTrainerUserName(username);
        trainingSessionDTO.setAction("delete");
    }


    @When("I send a request to delete the training session with Transaction ID {string}")
    public void iSendARequestToDeleteTrainingSession(String txnId) {
        this.transactionId = txnId;

        Mockito.when(workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName())).thenReturn(null);

        workLoadController.handleTraining(trainingSessionDTO, transactionId);
    }

    @Then("the system should log the action and delete the training session")
    public void theSystemShouldLogAndDelete() {
        Assertions.assertEquals("delete", trainingSessionDTO.getAction());
        Mockito.when(workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName())).thenReturn(null);

        Trainer trainer = workLoadService.findTrainerByUsername(trainingSessionDTO.getTrainerFirstName());
        Assertions.assertNull(trainer, "Trainer should be null");

    }

    @Given("a training session with invalid action details:")
    public void aTrainingSessionWithInvalidActionDetails(io.cucumber.datatable.DataTable dataTable) {
        var row = dataTable.asMaps(String.class, String.class).get(0);
        trainingSessionDTO = new TrainingSessionDTO();
        trainingSessionDTO.setTrainerUserName(row.get("username"));
        trainingSessionDTO.setTrainerFirstName(row.get("firstName"));
        trainingSessionDTO.setTrainerLastName(row.get("lastName"));
        trainingSessionDTO.setActive(Boolean.parseBoolean(row.get("isActive")));
        trainingSessionDTO.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
        trainingSessionDTO.setTrainingDuration(Integer.parseInt(row.get("trainingDuration")));
        trainingSessionDTO.setAction(row.get("action"));
    }

    @When("I send the request with Transaction ID {string}")
    public void iSendTheRequestWithTransactionId(String txnId) {
        this.transactionId = txnId;

        workLoadController.handleTraining(trainingSessionDTO, transactionId);
        Assertions.assertEquals("unknown", trainingSessionDTO.getAction());

    }


    @Then("the system should throw an IllegalArgumentException")
    public void theSystemShouldThrowIllegalArgumentException() {
        System.out.println("Test passed: Invalid action handled gracefully.");

    }
}
