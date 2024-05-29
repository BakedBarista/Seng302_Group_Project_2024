#Feature: U14 As Kaia, I want to know the current and future weather at my gardens, so I know if I need to water them
#
#  Background:
#    Given I am logged in as "Kaia"
#    And My email is "Kaia@gmail.com"
#    And My password is "P@ssw0rd"
#
#    #  AC1 Given I am on the garden details page for a garden I own,
#    #  then the current weather (including the current day of the week,
#    #  date, description i.e. sunny, overcast, raining with a relevant
#    #  image, temperature and humidity) for my location is shown.
#  Scenario: AC1 Display the current weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    Then The current weather is displaying for my location
#
#    #  AC2 Given I am on the garden details page for a garden I own,
#    #  then the future weather (including the day of the week, date, description i.e. sunny, overcast,
#    #  raining with a relevant image, temperature and humidity) for the future (3 to 5 days) is shown.
#  Scenario: AC2 Display the future weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    Then The weather for the next three days is displaying
#
#    #  AC3 Given the garden has a location that can’t be found, then an error message
#    #  tells me “Location not found, please update your location to see the weather”.
#  Scenario: AC3 Error message for unknown location
#    Given I create a garden called "Top Secret Garden" at "90" "Ilam rd", "Ilam", "unknown", "New Zealand"
#    When I am on the garden details page for that garden
#    Then An error message displays saying that the location cannot be found.
#
#    #  AC4 Given the past two days have been sunny, when I am on my garden details page,
#    #  then a highlighted element tells me “There hasn’t been any rain recently,
#    #  make sure to water your plants if they need it”.
#  Scenario: AC4 Plant care recommendations for sunny weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    And The past two days have been sunny for that location
#    Then I get a notification telling me to water my plants
#
#    #  AC5 Given the current weather is rainy, when I am on my garden details page,
#    #  then a highlighted element tells me “Outdoor plants don’t need any water today”.
#  Scenario: AC5 Plant care recommendations for rainy weather
#    Given I create a garden called "My Secret Garden" at "90" "Ilam rd", "Ilam", "Christchurch", "New Zealand". Latitude: "-43.521369", Longitude: "172.584920"
#    When I am on the garden details page for that garden
#    And The current weather is rainy
#    Then I get a notification telling me that outdoor plants do not need water that day
#
##  AC6 Given an element tells me I should or shouldn’t water my plants, when I click the
##  “x” or “close” button at the top right of the element, then the element is dismissed
##  and does not show up for that garden until the next day.
#
#  # There is a boolean for this