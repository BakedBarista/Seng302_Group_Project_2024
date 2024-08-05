Feature: U26 - As Lei, I want to be able to look up information about various plants so that I can learn more about them and whether they are suitable for my garden.

  Background:
    Given I am logged in as "Inaya"
    And My email is "Inaya@gmail.com"
    And My password is "P@ssw0rd"

  # Scenario: AC1 - From anywhere on the system there is a link to a form where I can search for plant information by name.
  #   Given I am anywhere on the system
  #   When I click on a link to search for a plant
  #   Then I am taken to a page where I can search for plant information by name

  Scenario: AC2 - When I enter a plant name, different names that match my input are shown as autocomplete options I can click on.
    Given I am on the plant search page
    When I search a plant name "tomato"
    Then different autocomplete suggestions pop up matching my search

  # Scenario: AC3 - If I search for a plant name that wasn't autocompleted and can't be found, then plants with similar names to what my input are shown.
  #   Given I am on the plant search page
  #   When I search a plant name with no autocomplete
  #   Then plants with similar names pop up

  # Scenario: AC4 - When I search for a plant that exists, then I see meaningful information about the plant, including an image.
  #   Given I am on the plant search page
  #   When I search a plant that exists
  #   Then meaningful information about that plant pops up

  # Scenario: AC5 - Given I search for a common plant name and the service is unavailable, when I finish typing the plant name, then there is an error message telling me "The plant information service is unavailable at the moment, please try again later" inside the drop down box.
  #   Given I search for a common plant name and the service is unavailable
  #   When I finish typing the plant name
  #   Then there is an error message telling me "The plant information service is unavailable at the moment, please try again later" inside the drop down box
