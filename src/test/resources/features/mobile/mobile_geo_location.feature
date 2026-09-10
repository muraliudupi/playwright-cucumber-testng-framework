Feature: Mobile - Geo Location

  @mobile @geolocation @require_login
  Scenario: User Logged In: Geo Location screen shows live coordinates and can toggle observing
    When the user opens Geo Location from the menu
    Then the Geo Location screen should be displayed
    And the latitude and longitude should be populated
    And the user stops observing the location
    And the user starts observing the location

  @mobile @geolocation @require_login
  Scenario: User Logged In: Geo Location screen reflects an injected device location
    When the user opens Geo Location from the menu
    Then the Geo Location screen should be displayed
    When the device location is set to latitude 37.4219999 and longitude -122.0840575
    Then the displayed coordinates should match latitude 37.4219999 and longitude -122.0840575