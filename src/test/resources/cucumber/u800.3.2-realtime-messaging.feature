Feature: Real-time Messaging
  Background:
    Given I am a user named "John"
    And There is a user named "Jane"
    And "John" and "Jane" are friends

  Scenario: AC1 - Sending messages
    Given I am on the direct-messaging page for "Jane"
    When I send a new message
    Then it shows up without me having to reload the page

  Scenario: AC2 - Receiving messages
    Given I am on the direct-messaging page for "Jane"
    When "Jane" sends me a message
    Then it shows up without me having to reload the page