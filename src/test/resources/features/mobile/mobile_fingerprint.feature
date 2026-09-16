Feature: Mobile - FingerPrint

  @mobile @fingerprint
  Scenario: FingerPrint: fingerprint screen shows an enabled toggle once a print is enrolled
    When the user opens FingerPrint from the menu
    Then the fingerprint screen should be displayed
    And the biometric toggle should be enabled

  @mobile @fingerprint
  Scenario: FingerPrint: user completes biometric authentication with the enrolled fingerprint
    When the user opens FingerPrint from the menu
    And the user enables the biometric toggle
    And the user authenticates with the enrolled fingerprint
    Then the biometric toggle should be turned on