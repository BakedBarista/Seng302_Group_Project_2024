Feature: As Inaya, I want to be able to make my garden public so that others can see what I’m growing.
  Background:
    Given I am logged in as "Inaya"
    And My email is "inaya@gmail.com"
    And My password is "P@ssw0rd"


  Scenario Outline: AC7 Description contains profanity
    Given I enter a new description "<description>"
    When Description contains profanity
    Then Error message "The description does not match the language standards of the app." is shown

    Examples:
      | description |
      | shit        |
      | fuck        |
      | @ss         |
      | sh!t        |

  Scenario Outline: AC7.1 Description contains no profanity
    Given I enter a new description "<description>"
    When Description not contain profanity
    Then Description is added

    Examples:
      | description      |
      | beautiful garden |
      | lovely tomatoes  |

  Scenario Outline: AC7.2 Description contains combination of good and bad words
    Given I enter a new description "<description>"
    When Description contains both profanity and good words
    Then Error message "The description does not match the language standards of the app." is shown

    Examples:
      | description                |
      | beautiful garden shit      |
      | lovely tomatoes fuck       |
      | amazing @ss plants         |
      | sh!t with lovely flowers   |