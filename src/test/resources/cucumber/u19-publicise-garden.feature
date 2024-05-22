Feature: As Inaya, I want to be able to make my garden public so that others can see what I’m growing.
  Background:
    Given I am logged in as "Inaya"
    And My email is "inaya@gmail.com"
    And My password is "P@ssw0rd"


  Scenario: AC7 Description contains profanity
      Given I enter a new description
      When Description contains profanity
      Then Error message "The description does not match the language standards of the app." is shown

  Scenario: AC7.1 Description contains no profanity
      Given I enter a new description
      When Description not contain profanity
      Then Description is added

  Scenario: AC7.2 Description contains combination of good and bad word
    Given I enter a new description
    When Description contains both profanity and good words
    Then Error message "The description does not match the language standards of the app." is shown