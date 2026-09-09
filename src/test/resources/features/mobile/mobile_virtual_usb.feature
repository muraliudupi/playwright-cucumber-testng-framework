Feature: Mobile - Virtual USB

  @mobile @virtualusb
  Scenario: Virtual USB: setup instructions display correctly from the menu
    When the user opens Virtual USB from the menu
    Then the Virtual USB screen should display the setup instructions