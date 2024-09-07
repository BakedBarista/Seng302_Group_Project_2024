Feature:As Lei, I want to display on my profile page a showcase of the best plants in my gardens so that I can better connect with people with similar favourite plants.
  Background:
    Given I am logged in as "Lei"
    And My email is "lei@gmail.com"
    And My password is "P@ssw0rdL3i"
    And I am on my edit profile page

  Scenario: AC1 Display favourite plants
    Given I am on my edit profile page
    Then I can see an editable section called “My Favourite Plants” where I can showcase my three favourite public plants that are mine

  Scenario: AC2 Add favourite plant
    Given I am on my edit profile page
    When I select a plant from the list of public plants
    Then The plant is displayed

