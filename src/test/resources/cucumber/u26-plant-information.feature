Feature: Plant Information

  Scenario: Given the plant information service is not available
    Given the plant information service is not available
    When I search for a plant named "tomato"
    Then I'm prompted with error message "Plant information service is unavailable at the moment, please try again later"
