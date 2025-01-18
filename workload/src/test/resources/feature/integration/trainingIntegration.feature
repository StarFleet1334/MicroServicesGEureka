Feature: End-to-end training session integration
  In order to confirm that Microservice #1 and Microservice #2 communicate via JMS
  we want to test the entire flow from REST request to MongoDB insertion.

  Background:
    Given ActiveMQ is running
    And Microservice #1 is running on port 8080
    And Microservice #2 is running on port 8081
    And I am logged in as admin with password admin on url "/api/login"

  Scenario: Create a training session
    Given I have a training type with name "Yoga Training"
    Given I have a trainer with firstName "John", lastName "Doe" and training type "Yoga Training"
    When I send a request to "/api/trainings/session" on Microservice #1 with:
      | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | action |
      | John             | Doe             | true     | 2025-10-10    | 2                | add    |
    Then the HTTP response code should be 201
    And Microservice #2 database should contain a training session with year "2025" with month "10" and with number "2"

  Scenario: Delete a training session
    Given I have a training type with name "Personal Training"
    Given I have a trainer with firstName "Alice", lastName "Smith" and training type "Personal Training"
    And I have a training session in Microservice #1 with:
      | trainerFirstName | trainerLastName | isActive | trainingDate  | trainingDuration | action |
      | Alice            | Smith           | true     | 2025-01-20    | 3                | add    |
    When I send a request to "/api/trainings/session" on Microservice #1 with:
      | trainerFirstName | trainerLastName | isActive | trainingDate  | trainingDuration | action |
      | Alice            | Smith           | true     | 2025-01-20    | 3                | delete |
    Then the HTTP response code should be 200
    And Microservice #1 database should NOT contain a training session for "trainer02" on date "2025-01-20"

  Scenario: Create trainer without specifying training type
    When I send a request to "/api/trainers" on Microservice #1 without Training type:
      | firstName | lastName | active |
      | Clark     | Kent     | true   |
    Then the HTTP response code should be 400
    And the response should say "Training Type `id` is required"


  Scenario: Create a training session in the past
    Given I have a training type with name "Zumba"
    Given I have a trainer with firstName "Kate", lastName "Beckinsale" and training type "Zumba"
    When I send a request to "/api/trainings/session" on Microservice #1 with:
      | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | action |
      | Kate             | Beckinsale      | true     | 2020-01-01   | 2               | add    |
    Then the HTTP response code should be 201
     And the database should contain a trainer with username "FallbackTrainer" instead of Kate.Beckinsale


  Scenario: Attempt to create a training session without logging in
    Given I do not have a valid admin session
    When I send a request to "/api/trainings/session" on Microservice #1 with:
      | trainerFirstName | trainerLastName | isActive | trainingDate | trainingDuration | action |
      | Tony             | Stark           | true     | 2025-01-15   | 2               | add    |
    Then the HTTP response code should be 401
