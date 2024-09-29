Feature: Unread Message Badges

  Background:
    Given I am a user named "John"
    And There is a user named "Jane"
    And There is a user named "Immy"
    And "John" and "Jane" are friends
    And "John" and "Immy" are friends

    Scenario: AC2 - Unread message badge displays
      Given I, "John", have a message from "Jane"
      When I am on the direct-messaging page for "Immy"
      Then I can see an unread message badge with "1" next to "Jane"

    Scenario: AC3 - Unread message badge disappears
      Given I, "John", have a message from "Jane"
      When I am on the direct-messaging page for "Jane"
      Then I do not see an unread message badge next to "Jane"
