Feature: Mobile ParaBank Customer Login

  @sanity @mobile @login @wip
  Scenario Outline: Successful mobile login using enterprise test data
    Given the user is on the mobile login screen
    When the user logs into the mobile app using credentials from data key "<TestCaseID>" sheet "LoginData"
    Then the mobile dashboard should be displayed

    Examples:
      | TestCaseID  |
      | Login_001   |