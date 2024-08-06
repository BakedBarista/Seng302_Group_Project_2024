Feature: U800.1.1 As Lei, I want to personalise and customise my profile page to show more about myself so that I can better connect with other gardening enthusiasts.

    Background:
        Given I am logged in as "Lei"
        And My email is "lei@gmail.com"
        And My password is "P@ssw0rdL3i"
        And I am on my edit profile page

    Scenario: AC2 - valid description
        When I enter a valid description
        And I click "Submit"
        Then the description is displayed on my profile page

    Scenario: AC3 - invalid description
        When I enter an invalid description that is too long
        Then an error message tells me “Your description must be less than 512 characters”

    Scenario: AC4 - valid profile banner
        Given I have a current cover photo
        When I choose a new, valid cover photo
        Then the photo displays on my profile

    Scenario: AC5 - profile banner of wrong file type
        When I submit a file that is not either a png, jpg or svg
        Then the banner is not submitted

    Scenario: AC6 - profile picture file too large
        When I submit a valid file with a size thats to large
        Then the profile picture is not submitted

    Scenario: AC7 - previous profile banner overwritten
        Given I have a current cover photo
        When I choose a new, valid cover photo
        And I click "Submit"
        Then the previous profile banner is overwritten and cannot be accessed anymore
