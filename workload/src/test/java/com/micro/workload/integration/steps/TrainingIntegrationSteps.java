package com.micro.workload.integration.steps;

import com.micro.workload.model.base.MonthSummary;
import com.micro.workload.model.base.Trainer;
import com.micro.workload.model.base.YearSummary;
import com.micro.workload.model.dto.TrainerRequestDTO;
import com.micro.workload.model.dto.TrainingSessionDTO;
import com.micro.workload.model.dto.TrainingTypeRequestDTO;
import com.micro.workload.model.dto.UserResponseDTO;
import com.micro.workload.repository.TrainerMongoRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainingIntegrationSteps {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingIntegrationSteps.class);

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String ACTUATOR_HEALTH_ENDPOINT = "/actuator/health";

    private ResponseEntity<String> lastResponse;
    private TrainingSessionDTO requestPayload;

    @Autowired
    private TrainerMongoRepository trainerMongoRepository;

    private String microservice1BaseUrl = "http://localhost:8080";
    private String microservice2BaseUrl = "http://localhost:8081";
    private String sessionCookie;
    private String trainerUserName;

    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }


    @Given("ActiveMQ is running")
    public void activemqIsRunning() {
        LOGGER.info("ActiveMQ is up and running...");
    }

    @Given("Microservice #1 is running on port {int}")
    public void microservice1IsRunningOnPort(int port) {
        this.microservice1BaseUrl = "http://localhost:" + port;
        verifyMicroserviceReadiness(this.microservice1BaseUrl, "Microservice #1");
    }

    @Given("Microservice #2 is running on port {int}")
    public void microservice2IsRunningOnPort(int port) {
        this.microservice2BaseUrl = "http://localhost:" + port;
        verifyMicroserviceReadiness(this.microservice2BaseUrl, "Microservice #2");
    }

    @Given("I am logged in as admin with password admin on url {string}")
    public void loginAsAdmin(String loginPath) {
        String loginUrl = microservice1BaseUrl + loginPath + "?username=admin&password=admin";

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, null, String.class);

        Assert.assertEquals("Invalid admin login credentials.", 200, loginResponse.getStatusCodeValue());

        this.sessionCookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        Assert.assertNotNull("Expected a Set-Cookie header from login.", this.sessionCookie);
    }

    @Given("I have a training type with name {string}")
    public void createTrainingType(String trainingTypeName) {
        TrainingTypeRequestDTO trainingTypeRequest = new TrainingTypeRequestDTO();
        trainingTypeRequest.setTrainingTypeName(trainingTypeName);

        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<TrainingTypeRequestDTO> requestEntity = new HttpEntity<>(trainingTypeRequest, headers);

        String url = microservice1BaseUrl + "/api/training-type";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        Assert.assertEquals("Failed to create training type.", 201, response.getStatusCodeValue());
        LOGGER.info("Training type '{}' successfully created.", trainingTypeName);
    }

    @Given("I have a trainer with firstName {string}, lastName {string} and training type {string}")
    public void iHaveTrainerWithTrainingType(String firstName, String lastName, String trainingTypeName) {
        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = microservice1BaseUrl + "/api/training-type";
        ResponseEntity<TrainingTypeRequestDTO[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TrainingTypeRequestDTO[].class);

        Assert.assertEquals("Failed to retrieve training types.", 200, response.getStatusCodeValue());
        TrainingTypeRequestDTO[] trainingTypes = response.getBody();

        Long trainingTypeId = null;
        for (TrainingTypeRequestDTO type : trainingTypes) {
            if (type.getTrainingTypeName().equals(trainingTypeName)) {
                LOGGER.info("CREATED TRAINING TYPE WAS: " + type.getTrainingTypeName() + ", TYPE_ID: " + type.getId());
                trainingTypeId = type.getId();
                break;
            }
        }
        Assert.assertNotNull("Training type not found: " + trainingTypeName, trainingTypeId);


        TrainerRequestDTO trainerRequestDTO = new TrainerRequestDTO();
        trainerRequestDTO.setFirstName(firstName);
        trainerRequestDTO.setLastName(lastName);
        trainerRequestDTO.setTrainingTypeId(trainingTypeId);
        trainerRequestDTO.setActive(true);


        url = microservice1BaseUrl + "/api/trainers";
        HttpEntity<TrainerRequestDTO> trainerRequestEntity = new HttpEntity<>(trainerRequestDTO, headers);
        ResponseEntity<UserResponseDTO> trainerResponse = restTemplate.postForEntity(url, trainerRequestEntity, UserResponseDTO.class);

        Assert.assertEquals("Trainer creation failed.", 201, trainerResponse.getStatusCodeValue());
        UserResponseDTO user = trainerResponse.getBody();
        if (user != null) {
            this.trainerUserName = user.getUsername();
            LOGGER.info("Created trainer with username: {}", this.trainerUserName);
        }
        LOGGER.info("Created trainer: {} {} with training type {}", firstName, lastName, trainingTypeName);
    }


    @When("I send a request to {string} on Microservice #1 with:")
    public void iSendARequestTo(String endpoint, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps(String.class, String.class).get(0);

        requestPayload = new TrainingSessionDTO();
        requestPayload.setTrainerUserName(this.trainerUserName);
        requestPayload.setTrainerFirstName(row.get("trainerFirstName"));
        requestPayload.setTrainerLastName(row.get("trainerLastName"));
        requestPayload.setActive(Boolean.valueOf(row.get("isActive")));
        requestPayload.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
        requestPayload.setTrainingDuration(Integer.valueOf(row.get("trainingDuration")));
        requestPayload.setAction(row.get("action"));



        HttpHeaders headers = createAuthenticatedHeaders();
        HttpEntity<TrainingSessionDTO> requestEntity = new HttpEntity<>(requestPayload, headers);

        String url = microservice1BaseUrl + endpoint;
        try {
            lastResponse = restTemplate.postForEntity(url, requestEntity, String.class);
            LOGGER.info("Received response from post on mongoDB: {}", lastResponse.getBody());
        } catch (HttpClientErrorException ex) {
            lastResponse = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
            LOGGER.info("Received error response: {}", lastResponse.getBody());
        }
        Assert.assertNotNull("No response received from Microservice #1.", lastResponse);

    }

    @Then("the HTTP response code should be {int}")
    public void theHTTPResponseCodeShouldBe(int expected) {
        Assert.assertNotNull("No HTTP response to verify.", lastResponse);
        Assert.assertEquals("Unexpected HTTP status code.", expected, lastResponse.getStatusCodeValue());
    }

    @And("Microservice #{int} database should contain a training session with year {string} with month {string} and with number {string}")
    public void microserviceDatabaseShouldContainATrainingSessionForWithYearWithMonthAndWithNumber(
            int microserviceNumber, String yearStr, String monthStr, String expectedHoursStr) {

        LOGGER.info("Finding Trainer with username: {}", this.trainerUserName);
        List<Trainer> list = trainerMongoRepository.findAll();
        for (Trainer x : list) {
            LOGGER.info("Found trainers in database with username: {}", x.getUsername());
        }
        Trainer trainer = trainerMongoRepository.findById(this.trainerUserName).orElse(null);
        Assert.assertNotNull("Trainer not found in the database", trainer);

        Map<Integer, YearSummary> yearSummaries = trainer.getYearSummaries();
        int year = Integer.parseInt(yearStr);
        Assert.assertTrue("Year " + year + " not found in trainer data", yearSummaries.containsKey(year));

        YearSummary yearSummary = yearSummaries.get(year);
        Map<Integer, MonthSummary> monthSummaries = yearSummary.getMonthSummaries();
        int month = Integer.parseInt(monthStr);
        Assert.assertTrue("Month " + month + " not found for the year " + year, monthSummaries.containsKey(month));

        MonthSummary monthSummary = monthSummaries.get(month);
        int actualHours = monthSummary.getTotalDuration();
        int expectedHours = Integer.parseInt(expectedHoursStr);
        Assert.assertEquals("Expected hours do not match actual hours for the month", expectedHours, actualHours);

        System.out.println("Validation successful: " + trainer);

    }

    @Then("Microservice #1 database should NOT contain a training session for {string} on date {string}")
    public void microservice2ShouldNotContainSession(String userName, String dateStr) {
        String url = microservice1BaseUrl + "/api/trainings/session";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie); // Authentication header
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<TrainingSessionDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                TrainingSessionDTO[].class
        );

        Assert.assertEquals(200, response.getStatusCodeValue());
        TrainingSessionDTO[] sessions = response.getBody();
        boolean found = false;
        for (TrainingSessionDTO s : sessions) {
            if (s.getTrainerUserName().equals(userName) &&
                    s.getTrainingDate().toString().equals(dateStr)) {
                found = true;
                break;
            }
        }
        Assert.assertFalse("Session unexpectedly found for user=" + userName, found);
    }

    private void verifyMicroserviceReadiness(String baseUrl, String serviceName) {
        String readinessUrl = baseUrl + ACTUATOR_HEALTH_ENDPOINT;
        RestTemplate restTemplate = new RestTemplate();

        try {
            LOGGER.info("Checking readiness of {} at {}", serviceName, readinessUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(readinessUrl, Map.class);
            String status = Objects.requireNonNull(response.getBody()).get("status").toString();

            if (!"UP".equals(status)) {
                throw new RuntimeException(serviceName + " is not ready. Health status: " + status);
            }

            Map<String, Object> components = (Map<String, Object>) response.getBody().get("components");
            LOGGER.info("{} readiness check successful. Components: {}", serviceName, components);

        } catch (Exception e) {
            LOGGER.error("Failed to verify the readiness of {}: {}", serviceName, e.getMessage());
            throw new RuntimeException("Failed to verify readiness of " + serviceName + " at: " + readinessUrl, e);
        }
    }


    @Given("I have a trainer with userName {string} firstName {string} lastName {string}")
    public void iHaveTrainer(String userName, String firstName, String lastName) {
        System.out.printf("Preparing trainer data: %s %s (username=%s)%n", firstName, lastName, userName);
    }


    @Given("I have a training session in Microservice #1 with:")
    public void iHaveATrainingSessionInMicroservice1(DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps(String.class, String.class).get(0);

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO();
        sessionDTO.setTrainerUserName(this.trainerUserName);
        sessionDTO.setTrainerFirstName(row.get("trainerFirstName"));
        sessionDTO.setTrainerLastName(row.get("trainerLastName"));
        sessionDTO.setActive(Boolean.valueOf(row.get("isActive")));
        sessionDTO.setTrainingDate(LocalDate.parse(row.get("trainingDate")));
        sessionDTO.setTrainingDuration(Integer.valueOf(row.get("trainingDuration")));
        sessionDTO.setAction("add");

        String url = microservice1BaseUrl + "/api/trainings/session";

        HttpHeaders headers = createAuthenticatedHeaders();

        HttpEntity<TrainingSessionDTO> requestEntity = new HttpEntity<>(sessionDTO, headers);


        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        Assert.assertNotNull("No response while adding a session in MS #1", response);

        Assert.assertEquals("Creating a new training session in MS #1 failed",
                201, response.getStatusCodeValue());
    }


    @When("I send a request to {string} on Microservice #1 without Training type:")
    public void iSendARequestToOnMicroserviceWithNoTrainingType(String endpoint, DataTable dataTable) {
        Map<String, String> row = dataTable.asMaps(String.class, String.class).get(0);

        TrainerRequestDTO dto = new TrainerRequestDTO();
        dto.setFirstName(row.get("firstName"));
        dto.setLastName(row.get("lastName"));
        dto.setActive(Boolean.parseBoolean(row.get("active")));

        String url = microservice1BaseUrl + endpoint;
        HttpHeaders headers = createAuthenticatedHeaders();

        HttpEntity<TrainerRequestDTO> requestEntity = new HttpEntity<>(dto, headers);
        try {
            lastResponse = restTemplate.postForEntity(url, requestEntity, String.class);
        } catch (HttpClientErrorException ex) {
            lastResponse = new ResponseEntity<>(ex.getResponseBodyAsString(), ex.getStatusCode());
            LOGGER.info("Received error response: {}", lastResponse.getBody());
        }

    }

    @And("the response should say {string}")
    public void theResponseShouldSay(String expectedMessage) {
        String body = lastResponse.getBody();
        LOGGER.info("Response body: {}", body);
        Assert.assertTrue("Expected message not found in response body.",
                body != null && body.contains(expectedMessage));
    }


    @And("the database should contain a trainer with username {string} instead of Kate.Beckinsale")
    public void theDatabaseShouldContainATrainerWithUsernameInsteadOf(String fallback) {
        Trainer fallbackTrainer = trainerMongoRepository.findById(fallback).orElse(null);

        Trainer unexpectedTrainer = trainerMongoRepository.findById("Kate.Beckinsale").orElse(null);

        Assert.assertNotNull("Expected trainer does not exist in the database: " + fallback, fallbackTrainer);
        LOGGER.info("Trainer with username '{}' found in the database.", fallback);

        Assert.assertNull("The unexpected trainer should not exist in the database: Kate.Beckinsale", unexpectedTrainer);
        LOGGER.info("Trainer with username 'Kate.Beckinsale' does not exist in the database, as expected.");
    }

    @Given("I do not have a valid admin session")
    public void iDoNotHaveAValidAdminSession() {
        this.sessionCookie = "";
    }

}
