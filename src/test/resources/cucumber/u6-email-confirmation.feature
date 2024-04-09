Feature: U6 As Sarah, I want to confirm my account by email when I register so that may account is more secure

  Scenario: AC1 - After registering, a token is sent to my email for confirming my email
    Given I submit a fully valid registration form
    When I click the register button
    Then A confirmation email is sent to my email address
    And A unique registration token is included in the email in the form of a unique signup code
    And I am presented with a page asking for the signup code

  Scenario: AC2 -
    Given A signup code has been created for a new user
    When 10 minutes have passed after the signup code was sent
    Then The token and account are deleted

  Scenario: AC2 -
    Given The token and account are deleted
    When I try to use the signup code
    Then I am informed that the signup code is invalid

  Scenario: AC3 -
    Given I have just registered
    And I have not confirmed my email
    When I try to log in
    Then I am presented with a page asking for the signup code

  Scenario: AC4 -
    Given I received a signup code
    When I introduce the signup code linked to my account
    Then The system validates the code successfully
    And I am redirected to the login page that tells me your account has been activated, please log in

  Scenario: AC5 -
    Given I received a signup code
    And have already validated my account
    When I reach the log in page, I am not asked to introduce my signup code anymore
    And I can log in to my account

  Scenario: AC6 -
    Given I receive a sign-up confirmation email
    When I did not create an account
    Then the email informs me that no actions need to be taken