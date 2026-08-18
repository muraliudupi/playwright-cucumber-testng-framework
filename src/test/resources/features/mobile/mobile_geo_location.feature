Feature: Mobile - Geo Location

  @mobile @geolocation @require_login
  Scenario: User Logged In: Geo Location screen shows live coordinates and can toggle observing
    When the user opens Geo Location from the menu
    Then the Geo Location screen should be displayed
    And the latitude and longitude should be populated
    And the user stops observing the location
    And the user starts observing the location