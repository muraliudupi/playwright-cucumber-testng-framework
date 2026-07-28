Feature: Account Funds Transfer

  @sanity @web @transfer
  Scenario Outline: Successful fund transfer between valid customer accounts
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "<TestCaseID>" sheet "TransferData"
    And the user navigates to the Transfer Funds interface
    And executes a transfer using data from data key "<TestCaseID>" sheet "TransferData"
    Then the transfer completes successfully with a validated dynamic confirmation message
    # DB Connectivity Issue
    And the backend database ledger state must reflect a transaction status of "SUCCESS"

    Examples:
      | TestCaseID   |
      | Transfer_001 |

  @web @transfer @negative
  Scenario: Transfer fails silently with an internal error when amount is missing
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user attempts a transfer with a missing amount
    Then the error message "An internal error has occurred and has been logged." is displayed

  @web @transfer @negative
  Scenario: Transfer fails silently with an internal error when amount is invalid
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user attempts a transfer with an invalid amount
    Then the error message "An internal error has occurred and has been logged." is displayed