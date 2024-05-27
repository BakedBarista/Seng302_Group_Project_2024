Feature: As Inaya, I want to be able to make my garden public so that others can see what I’m growing.

  Background:
    Given I am logged in as "Liam"
    And My email is "liam@gmail.com"
    And My password is "P@ssw0rd"

    Scenario: I have a garden and I want to make the garden public so others can see
        Given I have a garden
        When I make the garden public
        Then The garden is public

      Scenario: I want to edit someone else's garden
        Given A Garden Exists that is not mine
        When I try to edit the garden
        Then The garden is not edited and I am shown the access denied page
