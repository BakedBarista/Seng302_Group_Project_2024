#Feature: U14 As Kaia, I want to know the current and future weather at my gardens, so I know if I need to water them
#
#  Background:
#    Given I am logged in as "Kaia"
#    And My email is "Kaia@gmail.com"
#    And My password is "P@ssw0rd"
#
#  Scenario: AC1 Display the current weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    Then The current weather is displaying for my location
#
#  Scenario: AC2 Display the future weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    Then The weather for the next three days is displaying
#
#  Scenario: AC4 Plant care recommendations for sunny weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    And The past two days have been sunny for that location
#    Then I get a notification telling me to water my plants
#
#  Scenario: AC5 Plant care recommendations for rainy weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    And The current weather is "Rain"
#    Then I get a notification telling me that outdoor plants do not need water that day