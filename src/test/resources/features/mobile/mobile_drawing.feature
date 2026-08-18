Feature: Mobile - Drawing

  @mobile @drawing @require_login
  Scenario: User Logged In: Drawing screen accepts a stroke, clear, and save
    When the user opens Drawing from the menu
    Then the Drawing screen should be displayed
    And the user draws a stroke on the signature pad
    And the user clears the signature
    And the user draws a stroke on the signature pad
    And the user saves the signature
