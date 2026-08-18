Feature: Mobile - QR Code Scanner

  @mobile @qrscanner @require_login
  Scenario: User Logged In: QR code scanner opens from the menu
    When the user opens the QR code scanner from the menu
    Then the QR scanner screen should be displayed

  @mobile @qrscanner @require_login @wip @manual_only
  Scenario: User Logged In: Scanning a QR code opens the encoded URL
    # MANUAL VERIFICATION ONLY — not pursuing further automation.
    # To verify manually: Extended Controls > Camera > Virtual scene images,
    # load a QR code, position the camera to face it, then open the scanner
    # in-app and confirm it navigates to the encoded URL.
    Given the emulator virtual scene camera shows a QR code image
    When the user opens the QR code scanner from the menu
    Then a scanned QR code should navigate away from the scanner