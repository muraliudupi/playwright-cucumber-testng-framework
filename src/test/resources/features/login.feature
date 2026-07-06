@sanity @login
Feature: ParaBank Customer Login

  Scenario Outline: Successful login using external enterprise test data
    Given the user is on the ParaBank login page
    When the user logs in using credentials from excel row "<RowNumber>" sheet "LoginData"
    Then the Welcome message and the Accounts Overview page are displayed

    Examples:
      | RowNumber |
      | 1         |
      | 2         |