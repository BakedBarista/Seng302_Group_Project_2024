Feature: U24, As Inaya, I want to be able to browse gardens by different tags so that I can browse for gardens that match my interests.

  Background:
    Given I am logged in as "Inaya"
    And My email is "Inaya@gmail.com"
    And My password is "P@ssw0rd"
    And garden "test1" has a tag "testTag1"
    And garden "test2" has a tag "testTag2"

  Scenario: AC1 - Select a tag
    Given I am on the search public garden tag form
    When I enter a tag to search "testTag1"
    And I submit the public garden tag form
    Then garden "test1" is shown

  Scenario: AC1.1 - Select multiple tags
    Given I am on the search public garden tag form
    When I enter a tag to search "testTag1"
    And I enter a tag to search "testTag2"
    And I submit the public garden tag form
    Then garden "test1" is shown
    And garden "test2" is shown


  Scenario: AC2 - autocomplete search
    Given I am on the search public garden tag form
    When I enter a tag to search "test"
    Then An autocomplete tag of "testTag1" is shown

  Scenario: AC3 - autocomplete search click
    Given I am on the search public garden tag form
    When I enter a tag to search "test"
    And I click on autocomplete tag "testTag1"
    Then Tag "testTag1" is added to my search
    And Search form is cleared

  Scenario: AC4 - enter search
    Given I am on the search public garden tag form
    When I enter a tag to search "testTag1"
    And I press enter
    Then Tag "testTag1" is added to my search
    And Search form is cleared

  Scenario: AC5 - enter undefined tag
    Given I am on the search public garden tag form
    When I enter a tag to search "testTag2"
    And I press enter
    Then No new tag is added to search
    And Search form is not cleared
    And Error message is displayed 

Scenario: AC6 - search tag and name
    Given I am on the search public garden tag form
    When I enter a tag to search "testTag2"
    And I enter a garden name to search "test2"
    And I press enter
    Then The garden "test2" is displayed




