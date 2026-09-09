Feature: Mobile - FingerPrint

  @mobile @fingerprint
  Scenario: FingerPrint: biometrics alert displays and toggle remains disabled
    When the user opens FingerPrint from the menu
    Then the biometrics alert should be displayed
    When the user dismisses the biometric alert
    Then the fingerprint screen should be displayed
    And the biometric toggle should be disabled