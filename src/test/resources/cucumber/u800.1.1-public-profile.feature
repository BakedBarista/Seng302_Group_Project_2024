#Feature: U800.1.1 As Lei, I want to personalise and customise my profile page to show more about myself so that I can better connect with other gardening enthusiasts.
#
#    Background:
#        Given I am logged in as "Lei"
#        And My email is "lei@gmail.com"
#        And My password is "P@ssw0rdL3i"
#        And I am on my edit profile page
#
#    Scenario: AC2 - valid description
#        When I enter a valid description (that is not longer than 512 characters)
#        And I click "Save"
#        Then the description is displayed on my profile page
#
#    Scenario: AC3 - invalid description
#        When I enter a description that is longer than 512 characters
#        And I click "Save"
#        Then an error message tells me “Your description must be less than 512 characters”
#
#    Scenario: AC4 - valid profile banner
#        When I choose a new cover photo of type (.jpg, .jpeg, .png, .svg) and less than 10MB
#        And I click "Save"
#        Then the photo displays in the cover photo section behind my profile picture (e.g. like a LinkedIn profile)
#
#    Scenario: AC5 - profile banner of wrong file type
#        When I submit a file that is not either a png, jpg or svg
#        Then an error message tells me “Image must be of type png, jpg or svg”
#
#    Scenario: AC6 - profile banner file too large
#        When I submit a valid file with a size of more than 10MB
#        Then an error message tells me “Image must be less than 10MB”
#
#    Scenario: AC7 - previous profile banner overwritten
#        When I choose a new cover photo of type (.jpg, .jpeg, .png, .svg) and less than 10MB
#        And I click "Save"
#        Then the previous profile banner is overwritten and cannot be accessed anymore
