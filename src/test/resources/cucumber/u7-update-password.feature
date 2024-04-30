Feature: U7 As Sarah, I want to be able to change my password, so that I can keep my account secured with a new password in case my password gets leaked

  Background:
    Given I am logged in as "Sarah"
    And My email is "sarah@gmail.com"
    And My password is "P@ssw0rd"

  Scenario: AC5.1 - Password is updated
    Given I am on the change password form
    When I enter my old password "P@ssw0rd"
    And I enter a new password "N3wP@ssw0rd"
    And I submit the change password form
    Then My password is updated to "N3wP@ssw0rd"

  Scenario: AC5.2 - Confirmation email is sent
    Given I am on the change password form
    When I enter my old password "P@ssw0rd"
    And I enter a new password "N3wP@ssw0rd"
    And I submit the change password form
    Then An email is sent to my inbox to confirm that my password has been changed
