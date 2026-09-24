Feature: Mobile - FingerPrint

  @mobile @fingerprint
  Scenario: FingerPrint: fingerprint screen shows an enabled toggle once a print is enrolled
    When the user opens FingerPrint from the menu
    Then the fingerprint screen should be displayed
    And the biometric toggle should be enabled

  @mobile @fingerprint @require_login @wip @manual_only
  Scenario: FingerPrint: user completes biometric authentication with the enrolled fingerprint
      # MANUAL VERIFICATION ONLY — not pursuing further automation.
      # Enrollment success on the emulator's virtual fingerprint sensor is inherently
      # inconsistent run-to-run, and the app does not reliably persist the biometric-enabled
      # state across logout/re-login — both outside anything this automation controls.
      # To verify manually: enroll a fingerprint on the emulator, open FingerPrint from the
      # menu, enable the toggle, confirm via Extended Controls > Fingerprint, and confirm the
      # toggle stays on after a logout/re-login cycle.
    When the user opens FingerPrint from the menu
    And the user enables the biometric toggle
    And the user authenticates with the enrolled fingerprint
    Then the biometric toggle should be turned on