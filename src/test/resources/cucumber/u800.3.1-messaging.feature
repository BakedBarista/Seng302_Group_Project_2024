Feature: Message friend from the friends list
    Scenario: User clicks the message button and is taken to the message page
      Given I am viewing my friends list
      When I click the "message" button next to one of my friends
      Then I am taken to the message page