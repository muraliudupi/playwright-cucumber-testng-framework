Feature: ParaBank Logout

  @sanity @web @logout
  Scenario: Log out successfully after logging in
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user logs out
    Then the user is returned to the login page