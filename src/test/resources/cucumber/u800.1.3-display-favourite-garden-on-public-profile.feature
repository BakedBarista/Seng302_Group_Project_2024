Feature: As Lei, I want to display on my profile page my favourite public garden of mine so that I can better connect with gardening enthusiasts and show off my hard work in my proudest garden.
  Background:
    Given I am logged in as "Lei"
    And My email is "lei@gmail.com"
    And My password is "P@ssw0rdL3i"
    And I am on my edit profile page

    Scenario: AC1 Display favourite garden
      Given I am on my edit profile page
      Then I can see an editable section called “My Favourite Garden” where I can showcase my favourite public garden of mine.

    Scenario: AC3 Add favourite garden
      Given I am on my edit profile page
      When I select a garden from the list of public gardens
      Then The garden is displayed

    Scenario: AC4 Update favourite garden
      Given I am on my edit profile page and I already have a favourite garden
      When I select a garden from the list of public gardens
      Then My favourite garden is updated
