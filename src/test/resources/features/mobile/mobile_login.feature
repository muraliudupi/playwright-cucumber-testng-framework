Feature: Mobile - Customer Login

  @mobile @login
  Scenario Outline: Successful mobile login using enterprise test data
    Given the user is on the mobile login screen
    When the mobile user logs into the mobile app using credentials from data key "<TestCaseID>" sheet "MobileLoginData"
    Then the mobile dashboard should be displayed

    Examples:
      | TestCaseID |
      | Login_001  |

  @mobile @logout @require_login
  Scenario: Successful logout from mobile app
    When the user logs out from the mobile app
    Then the login screen should be displayed

  @mobile @login @negative
  Scenario Outline: Login fails when username or password are empty
    Given the user is on the mobile login screen
    When the user attempts login with username "<username>" and password "<password>"
    Then the login error "<message>" should be displayed

    Examples:
      | username | password | message              |
      |          |          | Username is required |
      |          | demo     | Username is required |
      | john     |          | Enter Password       |