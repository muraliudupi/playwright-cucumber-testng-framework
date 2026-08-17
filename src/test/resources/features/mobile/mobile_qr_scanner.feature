Feature: Mobile - QR Code Scanner

  @mobile @qrscanner @require_login
  Scenario: User Logged In: QR code scanner opens from the menu
    When the user opens the QR code scanner from the menu
    Then the QR scanner screen should be displayed

  @mobile @qrscanner @require_login @wip
  Scenario: User Logged In: Scanning a QR code opens the encoded URL
    Given the emulator virtual scene camera shows a QR code image
    When the user opens the QR code scanner from the menu
    Then a scanned QR code should navigate away from the scanner
