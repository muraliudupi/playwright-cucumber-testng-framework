Feature: Mobile - About Screen

  @mobile @about @require_login
  Scenario: User Logged In: About screen displays app information from the menu
    When the user opens the About screen from the menu
    Then the About screen should display the app title, version, team, and website information