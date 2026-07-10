Feature: Account Funds Transfer

  @sanity @web @transfer
  Scenario Outline: Successful fund transfer between valid customer accounts
    Given the user is on the ParaBank login page
    When the user logs in using credentials from data key "<TestCaseID>" sheet "TransferData"
    And the user navigates to the Transfer Funds interface
    And executes a transfer using data from data key "<TestCaseID>" sheet "TransferData"
    Then the transfer completes successfully with a validated dynamic confirmation message
    # DB Connectivity Issue
    And the backend database ledger state must reflect a transaction status of "SUCCESS"

    Examples:
      | TestCaseID   |
      | Transfer_001 |