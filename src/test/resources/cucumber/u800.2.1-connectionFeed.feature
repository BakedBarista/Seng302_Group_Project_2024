Feature: U800.2.1 As Favian, I want to find potential friends and send them friend requests so that I can better connect with other gardening enthusiasts.

    Background:
        Given I am logged in as "Favian"
        And My email is "Favian@gmail.com"
        And My password is "P@ssw0rdF4v"

    Scenario: AC1 - no duplication of profiles
        Given I am on the homepage looking at the list of user profiles
        When I accept or decline a profile
        Then I am not shown that profile again

    Scenario: AC2 - deny a user no request incoming 
        Given I am logged in and I am on the homepage looking at the stack of user profiles
        When I click the red cross button on a person who I do not have a pending friend request from
        Then this card is removed from the stack and no friend request is sent

    Scenario: AC3 - deny a user with a request incoming 
        Given I am logged in and I am on the homepage looking at the stack of user profiles
        When I click the red cross button on a person who I do have a pending friend request from
        Then this card is removed from the stack and their friend request is declined

    Scenario: AC4 - accept a new connection
        Given I am logged in and I am on the homepage looking at the stack of user profiles
        When I click the green love heart button on a person who I have a pending friend request from
        Then the friend request is accepted and a confirmation message pops up, and then the next user profile is shown

    Scenario: AC5 - accept a conneciton send a request
        Given I am logged in and I am on the homepage looking at the stack of user profiles
        When I click the green love heart button on a person who I don’t have a pending friend request from
        Then a friend request is sent to the user that was on the top of the stack and then the next profile is shown

    Scenario: AC6 - friend requests first
        Given I am logged in and on the home page
        When I am viewing the potential new connections stack
        Then the stack will start with profiles of users who have sent me a friend request

    Scenario: AC7 -  flipping card details
        Given I am logged in and I am on the homepage looking at the top profile card on the stack
        When I tap on the card
        Then the card will flip over and the Best Plants will be displayed
        And Favourite Garden will be displayed

    Scenario: AC8 - showing details
        Given I am logged in 
        When I am on the homepage looking at the top profile card on the stack
        Then initially the profile picture, name, and description are shown.

    Scenario: AC9 - swipeable?
        Given I am logged in and on the home page
        Then I am shown potential new connections who are not my friends as a swipeable stack of profile cards.

    Scenario: AC10 - default message
        Given I am on the homepage looking at the list of user profiles
        When there are no profiles to view
        Then I am shown a message saying “Could not find any connection suggestions”.