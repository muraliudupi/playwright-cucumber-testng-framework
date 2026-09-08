Feature: Mobile - FingerPrint

  @mobile @fingerprint
  Scenario: FingerPrint: toggling on an unsupported device shows a biometrics alert
    When the user opens FingerPrint from the menu
    And the biometric unsupported alert should be displayed
    When the user dismisses the biometric alert
    Then the fingerprint screen should be displayed
    Then the biometric toggle should be disabled