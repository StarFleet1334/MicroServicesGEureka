package com.micro.workload.steps.trainer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.service.impl.TrainerService;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TrainerControllerSteps {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    private MvcResult mvcResult;
    private Exception capturedException;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --------------------------------------------------------
    // G I V E N
    // --------------------------------------------------------

    @Given("a trainer with username {string} exists")
    public void a_trainer_with_username_exists(String username) {
        Trainer mockTrainer = new Trainer(username, "John", "Doe", true);

        Mockito.when(trainerService.getTrainerSummary(username))
                .thenReturn(mockTrainer);
    }

    @Given("no trainer exists with username {string}")
    public void no_trainer_exists_with_username(String username) {
        Mockito.when(trainerService.getTrainerSummary(username))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found for " + username));
    }

    @Given("trainers exist in the system")
    public void trainers_exist_in_the_system() {
        Mockito.when(trainerService.getAllTrainers())
                .thenReturn(Arrays.asList(
                        new Trainer("user1", "John", "Doe", true),
                        new Trainer("user2", "Jane", "Doe", true)
                ));
    }

    @Given("no trainers exist")
    public void no_trainers_exist() {
        Mockito.when(trainerService.getAllTrainers())
                .thenReturn(Collections.emptyList());
    }

    @Given("trainers exist with first name {string} and last name {string}")
    public void trainers_exist_with_first_name_and_last_name(String firstName, String lastName) {
        Mockito.when(trainerService.getTrainersByName(firstName, lastName))
                .thenReturn(Arrays.asList(
                        new Trainer("user123", firstName, lastName, true)
                ));
    }

    @Given("no trainers exist with first name {string} and last name {string}")
    public void no_trainers_exist_with_first_name_and_last_name(String firstName, String lastName) {
        Mockito.when(trainerService.getTrainersByName(firstName, lastName))
                .thenReturn(Collections.emptyList());
    }

    // --------------------------------------------------------
    // W H E N
    // --------------------------------------------------------

    @When("I request the trainer summary for username {string}")
    public void i_request_the_trainer_summary_for_username(String username) throws Exception {
        try {
            mvcResult = mockMvc.perform(get("/trainers/{username}", username))
                    .andReturn();
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("I request all trainers")
    public void i_request_all_trainers() throws Exception {
        mvcResult = mockMvc.perform(get("/trainers"))
                .andReturn();
    }

    @When("I search for trainers by first name {string} and last name {string}")
    public void i_search_for_trainers_by_first_name_and_last_name(String firstName, String lastName) throws Exception {
        mvcResult = mockMvc.perform(get("/trainers/search")
                        .param("firstName", firstName)
                        .param("lastName", lastName))
                .andReturn();
    }

    // --------------------------------------------------------
    // T H E N
    // --------------------------------------------------------

    @Then("the response should have a status code of {int}")
    public void the_response_should_have_a_status_code_of(int expectedStatusCode) {
        Assert.assertNotNull("mvcResult should not be null", mvcResult);
        int actualStatus = mvcResult.getResponse().getStatus();
        Assert.assertEquals("HTTP status code mismatch", expectedStatusCode, actualStatus);
    }

    @Then("the response includes trainer details for {string}")
    public void the_response_includes_trainer_details_for(String username) throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        Trainer trainer = objectMapper.readValue(json, Trainer.class);

        Assert.assertNotNull("Trainer should not be null", trainer);
        Assert.assertEquals("Username mismatch", username, trainer.getUsername());
    }

    @Then("the response includes multiple trainers")
    public void the_response_includes_multiple_trainers() throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        Collection<Trainer> trainers = objectMapper.readValue(json, new TypeReference<List<Trainer>>() {
        });

        Assert.assertFalse("Expected multiple trainers, but none found", trainers.isEmpty());
        Assert.assertTrue("Should have more than one trainer", trainers.size() > 1);
    }

    @Then("the response contains no trainers")
    public void the_response_contains_no_trainers() throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        Collection<Trainer> trainers = objectMapper.readValue(json, new TypeReference<List<Trainer>>() {
        });

        Assert.assertTrue("Expected no trainers, but found some", trainers.isEmpty());
    }

    @Then("the response includes trainers with names {string} and {string}")
    public void the_response_includes_trainers_with_names_and(String firstName, String lastName) throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        List<Trainer> trainers = objectMapper.readValue(json, new TypeReference<List<Trainer>>() {
        });

        Assert.assertFalse("Expected at least one trainer", trainers.isEmpty());
        Assert.assertTrue("All trainers should match the given names",
                trainers.stream().allMatch(t -> t.getFirstName().equals(firstName)
                        && t.getLastName().equals(lastName)));
    }

    @And("the response includes no trainers")
    public void theResponseIncludesNoTrainers() throws Exception {
        String json = mvcResult.getResponse().getContentAsString();
        List<Trainer> trainers = objectMapper.readValue(json, new TypeReference<List<Trainer>>() {
        });

        Assert.assertTrue("Expected no trainers in the response, but got some", trainers.isEmpty());
    }
}
