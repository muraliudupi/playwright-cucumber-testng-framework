Feature: Mobile - WebView

  @mobile @webview @require_login @test
  Scenario: User Logged In: WebView loads a configured URL
    When the user opens WebView from the menu
    Then the WebView screen should be displayed
    And the user navigates to the configured test site in the WebView
    Then the page load should complete
    And the loaded page should match the configured test site