Feature: Sending images in messages
  Background:
    Given I am a user named "John"
    And There is a user named "Jeff"
    And "John" and "Jeff" are friends

  Scenario: AC3 - Sending an image to a friend
    Given I am on a direct messaging page for my friend "Jeff"
    And I am on the direct-messaging page for "Jeff"
    When I send an image
    Then it shows up without me having to reload the page

#  Scenario: AC3 - Receiving an image from a friend
#    Given I am on the direct-messaging page for "Jane"
#    When "Jane" sends me an image
#    Then it shows up without me having to reload the page