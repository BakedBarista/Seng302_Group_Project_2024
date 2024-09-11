Feature:As Lei, I want to display on my profile page a showcase of the best plants in my gardens so that I can better connect with people with similar favourite plants.
  Background:
    Given I am on my edit profile page
    And I have setup favourite plant feature

  Scenario: AC1 Display favourite plants
    Given I am on my edit profile page
    Then I can see an editable section called “My Favourite Plants” where I can showcase my three favourite public plants that are mine

  Scenario: AC2 Add favourite plant
    Given I am on my edit profile page
    When I select "apple" from the list of public plants
    Then "apple" is favourited

  Scenario: AC5 I can delete favourite plants
    Given I am on my edit profile page
    And I select "apple" from the list of public plants
    And I select "banana" from the list of public plants
    And I select "orange" from the list of public plants
    When I click the delete button on "apple"
    Then I no longer have "apple" favourited
