Feature: Web - Customer Login

  @sanity @web @login
  Scenario Outline: Successful login using external enterprise test data
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "<TestCaseID>" sheet "LoginData"
    Then the Welcome message and the Accounts Overview page are displayed

    Examples:
      | TestCaseID |
      | Login_001  |

  @sanity @web @login @negative
  Scenario Outline: Login fails when required credentials are missing
    Given the user is on the ParaBank login page
    When the web user logs in with username "<username>" and password "<password>"
    Then the login error message "Please enter a username and password." is displayed

    Examples:
      | username | password |
      |          |          |
      | john     |          |
      |          | demo     |