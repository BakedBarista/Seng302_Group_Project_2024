Feature: U8 As Kaia, I want to create a record of my Garden so that I can manage all my gardening tasks

    Background:
        Given I am logged in as "Kaia"
        And My email is "Kaia@gmail.com"
        And My password is "P@ssw0rd"


    Scenario: AC2 Create a garden
        Given I am on the create garden form
        When I enter valid name "liam"
        And I enter a valid street number "1"
        And I enter a valid street name "johns"
        And I enter a valid suburb "ilam"
        And I enter a valid city "christchurch"
        And I enter a valid country "australia"
        And I submit create garden form
        Then A garden with that information is created

    Scenario: AC2 Create a garden with lat and long
        Given I am on the create garden form
        When I enter valid name "liam"
        And I enter a valid street number "1"
        And I enter a valid street name "johns"
        And I enter a valid suburb "ilam"
        And I enter a valid city "christchurch"
        And I enter a valid country "australia"
        And I enter a valid lat "100"
        And I enter a valid lon "200"
        And I submit create garden form
        Then A garden with that information is created