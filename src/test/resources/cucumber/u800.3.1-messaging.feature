Feature: Message friend from the friends list
  Background:
    Given I am a user named "John"
    And There is a user named "Jane"
    And "John" and "Jane" are friends

    Scenario: User clicks the message button and is taken to the message page
      Given I am viewing my friends list
      When I click the message button next to my friend "Jane"
      Then I am taken to the message page

    Scenario: User sends a message to their friend
      Given I am on a direct messaging page for my friend "Jane"
      When I have typed a text-based message "Hello"
      And I press Send
      Then I am taken to the message page
      And The message is sent to that friend.

    Scenario Outline: User sends invalid message to their friend
      Given I am on a direct messaging page for my friend "Jane"
      When I have an empty message <message>
      And I send invalid message
      Then I am taken to the message page
      And The message is not sent.

      Examples:
      |message|
      |""     |
      |"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"|