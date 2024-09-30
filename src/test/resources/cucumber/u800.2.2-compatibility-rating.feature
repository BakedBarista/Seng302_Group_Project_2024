Feature: U800.2.2 As Favian, I want my feed to suggest more people who are interested in the same type of plants as me and who are close to me so that I can have more meaningful interactions with the people I connect with.

    Background:
        Given I am logged in as "Favian"
        And My email is "Favian@gmail.com"
        And My password is "P@ssw0rdF4v"

     Scenario: AC1 - ordering of profiles
         Given I am on the home page
         Then the profiles that are shown below the users who have sent me friend requests are ranked by a combination of geographic proximity, similarity of plants we grow, and similarity of age

    Scenario: AC2 - friendship compatibility shown to user
        Given I am on the home page
        Then I can see the friendship compatibility rating between the friend candidate and me, as a percentage
