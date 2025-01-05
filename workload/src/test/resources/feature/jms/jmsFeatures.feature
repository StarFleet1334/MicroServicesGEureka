Feature: Process training session messages from the queue
  As a system
  I want to consume messages from the ActiveMQ queue
  So that training session actions are processed correctly

  Background:
    Given the ActiveMQ queue is ready to receive messages

  Scenario: Successfully process a training added event
    Given a valid "add" training message is sent to the queue
    When the JmsConsumer processes the message
    Then the training is added to both workload services
    And the logs contain "Received training added event"

  Scenario: Successfully process a training deleted event
    Given a valid "delete" training message is sent to the queue
    When the JmsConsumer processes the message
    Then the training is deleted from both workload services
    And the logs contain "Received training deleted event"

  Scenario: Receive an invalid action type
    Given an invalid action type message is sent to the queue
    When the JmsConsumer processes the message
    Then an "Invalid action specified" error is logged
    And no actions are performed on the workload services

  Scenario: Receive an invalid message format
    Given a message with invalid format is sent to the queue
    When the JmsConsumer processes the message
    Then an "Error processing message" error is logged
    And no actions are performed on the workload services
