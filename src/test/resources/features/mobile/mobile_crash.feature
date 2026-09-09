Feature: Mobile - Crash App (Debug)

  @mobile @crashapp
  Scenario: Crash App: Crashes screen displays both trigger buttons
    # Verification only — the crash-trigger buttons are intentionally NOT tapped,
    # since doing so would terminate the app process and destabilize the test run.
    When the user opens Crash app from the menu
    Then the Crashes screen should display both crash trigger buttons