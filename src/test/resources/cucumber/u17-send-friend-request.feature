Feature:As Liam, I want to connect with my friends on Gardener’s Grove so that we can build a community on the app.

  Background:
    Given I am logged in as "Liam"
    And My email is "liam@gmail.com"
    And My password is "P@ssw0rd"

  Scenario: I want to search for a user to add as a friend
    Given I am on the manage friends page
    When I search "John Doe"
    Then I can see "John Doe" on the search list

  Scenario: I search for a user that does not exist on the database
    Given I am on the manage friends page
    When I search "Ben Doe"
    Then I don't see "Ben Doe" on the search list

