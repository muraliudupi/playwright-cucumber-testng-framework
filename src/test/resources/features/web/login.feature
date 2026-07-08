Feature: ParaBank Customer Login

  @sanity @web @login
  Scenario Outline: Successful login using external enterprise test data
    Given the user is on the ParaBank login page
    When the user logs in using credentials from data key "<TestCaseID>" sheet "LoginData"
    Then the Welcome message and the Accounts Overview page are displayed

    Examples:
      | TestCaseID  |
      | Login_001   |
      | Login_002   |