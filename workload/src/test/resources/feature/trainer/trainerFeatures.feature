Feature: Trainer management

  Scenario: Successfully retrieve a trainer by username
    Given a trainer with username "johnDoe" exists
    When I request the trainer summary for username "johnDoe"
    Then the response should have a status code of 200
    And the response includes trainer details for "johnDoe"

  Scenario: Trainer not found by username
    Given no trainer exists with username "unknownUser"
    When I request the trainer summary for username "unknownUser"
    Then the response should have a status code of 404

  Scenario: Retrieve all trainers successfully
    Given trainers exist in the system
    When I request all trainers
    Then the response should have a status code of 200
    And the response includes multiple trainers

  Scenario: No trainers exist in the system
    Given no trainers exist
    When I request all trainers
    Then the response should have a status code of 200
    And the response contains no trainers

  Scenario: Search trainers by name successfully
    Given trainers exist with first name "Jane" and last name "Doe"
    When I search for trainers by first name "Jane" and last name "Doe"
    Then the response should have a status code of 200
    And the response includes trainers with names "Jane" and "Doe"

  Scenario: No trainers found by search criteria
    Given no trainers exist with first name "Nonexistent" and last name "User"
    When I search for trainers by first name "Nonexistent" and last name "User"
    Then the response should have a status code of 200
    And the response includes no trainers
