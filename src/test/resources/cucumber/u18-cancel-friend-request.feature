Feature: U18 As Liam, I want to cancel friends on Gardener’s Grove so that we can manage my friends list with people I trust


Background:
  Given I am logged in as "Liam"
  And My email is "liam@gmail.com"
  And My password is "P@ssw0rd"


  Scenario: I have sent a request to the wrong friend, and I want to cancel the request
    Given I send a friend request to "John" "Doe"
    When I click on the cancel friend request button
    Then I should not see "John" "Doe" in my friends list
    And I should not see "John" "Doe"  in my pending friend requests list

