Feature: Mobile - Reset App State

  @mobile @resetapp
  Scenario: Reset App State: user confirms and resets application data
    When the user opens Reset App State from the menu
    Then the reset app confirmation alert should be displayed
    When the user confirms the app reset
    Then the reset app success alert should be displayed
    When the user dismisses the reset success alert

  @mobile @resetapp
  Scenario: Reset App State: user cancels the reset and remains on the catalog
    When the user opens Reset App State from the menu
    Then the reset app confirmation alert should be displayed
    When the user cancels the app reset
    Then the mobile product catalog should still be displayed