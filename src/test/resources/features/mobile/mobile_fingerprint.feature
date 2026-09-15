Feature: Mobile - FingerPrint

  @mobile @fingerprint
  Scenario: FingerPrint: biometrics alert displays and toggle is enabled once a print is enrolled
    When the user opens FingerPrint from the menu
    Then the biometrics alert should be displayed
    When the user dismisses the biometric alert
    Then the fingerprint screen should be displayed
    And the biometric toggle should be enabled

  @mobile @fingerprint
  Scenario: FingerPrint: user completes biometric authentication with the enrolled fingerprint
    When the user opens FingerPrint from the menu
    And the user dismisses the biometric alert
    And the user enables the biometric toggle
    And the user authenticates with the enrolled fingerprint
    Then the biometric toggle should be turned on