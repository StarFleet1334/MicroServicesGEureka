Feature: Manage Training Sessions
  As an admin
  I want to manage training sessions for trainers
  So that I can add or delete training sessions efficiently

  Scenario: Add a new training session
    Given a trainer with the following details:
      | username  | firstName | lastName | isActive | trainingDate | trainingDuration | action |
      | trainer01 | John      | Doe      | true     | 2025-01-03   | 2                | add    |
    When I send a request to add the training session with Transaction ID "TXN-123"
    Then the system should log the action and save the training session

  Scenario: Delete an existing training session
    Given an existing trainer with username "trainer01"
    When I send a request to delete the training session with Transaction ID "TXN-456"
    Then the system should log the action and delete the training session

  Scenario: Invalid action specified
    Given a training session with invalid action details:
      | username     | firstName   | lastName   | isActive | trainingDate | trainingDuration | action  |
      | trainer02    | Alice       | Smith      | true     | 2025-01-01   | 3                | unknown |
    When I send the request with Transaction ID "TXN-789"
    Then the system should throw an IllegalArgumentException
