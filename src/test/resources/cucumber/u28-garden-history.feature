Feature: U28, As Kaia, I want to keep track of planting history recording when I plant and harvest different vegetables (and other plants) so that I can follow the evolution of my plants.

    Background:
        Given I am logged in as "Kaia"
        And My email is "Kaia@gmail.com"
        And My password is "P@ssw0rd"
        And I have a garden named "garden1"

    Scenario: AC1 - planting date valid
        Given I am on the add plant form
        When I enter a valid plant name "plant1" and a valid date "2020-01-01"
        And I submit the add plant form
        Then the plant is successfully added

    Scenario: AC2 - planting date invalid
        Given I am on the add plant form
        When I enter a valid plant name "plant2" and a invalid date "2030-02-01"
        And I submit the add plant form
        Then the plant is not added

    Scenario: AC3 - planting harvest date default
        Given I am browsing my recorded plants for garden "garden1"
        And "plant1" has no harvested date
        When I select a plant "plant1" to be harvested
        And I do not change the default date
        And I submit the harvest date form
        Then The plant is marked harvested on today's date

    Scenario: AC4 - planting harvest date past
        Given I am browsing my recorded plants for garden "garden1"
        And "plant1" has no harvested date
        When I select a plant "plant1" to be harvested
        And I change the date to yesterday's date
        And I submit the harvest date form
        Then The plant is marked harvested on yesterday's date

    Scenario: AC5 - planting harvest date future
        Given I am browsing my recorded plants for garden "garden1"
        And "plant1" has no harvested date
        When I select a plant "plant1" to be harvested
        And I change the date to tomorrow's date
        And I submit the harvest date form
        Then The plant is not marked harvested
#
#    Scenario: AC6 - planting record add image
#        Given I am browsing my recorded plants
#        And I select a plant "plant1" that has not been harvested
#        And has no image on record for today
#        When I add a image on the record and submit
#        Then the image is on the record
#
#    Scenario: AC7 - planting record add image, image exists
#        Given I am browsing my recorded plants
#        And I select a plant "plant1" that has not been harvested
#        And has a image on record for today
#        When I add a image on the record and submit
#        Then the image is not added to the record
#
#    Scenario: AC8 - planting record add description
#        Given I am browsing my recorded plants
#        And I select a plant "plant1" that has not been harvested
#        When I add a description on the record and submit
#        Then the description is added to the record
#
#    Scenario: AC9 - planting record dont add description
#        Given I am browsing my recorded plants
#        And I select a plant "plant1" that has not been harvested
#        When I leave the description on the record empty and submit
#        Then the record has no description

    Scenario: AC10 - garden timeline
        Given I am on the garden detials page for "garden1"
        When I view my gardens timeline
        Then there is a record of plant history for "garden1"

#    Scenario: AC11 - view plant timeline with images and descriptions
#        Given I view my plant
#        And the plant has records with images and descriptions
#        When I view my plant's timeline
#        Then I am presented with a timeline of specific plant records
#        And each record includes dates, images, and descriptions of the plant at these dates
#
#    Scenario: AC12 - harvest and replant a plant in a different spot
#        Given I have a plant in a spot of my garden
#        When I harvest my plant and replant it in a different spot of my garden
#        Then my plant is not in its original spot
#        And my plant is in its new spot
