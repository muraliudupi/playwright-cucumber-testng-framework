Feature: Account Funds Transfer

  @all @sanity @transfer
  Scenario Outline: Successful fund transfer between valid customer accounts
    Given the user is on the ParaBank login page
    When the user logs in using credentials from excel row "<RowNumber>" sheet "LoginData"
    And the user navigates to the Transfer Funds interface
    And executes a transfer using data from excel row "<RowNumber>" sheet "TransferData"
    Then the transfer completes successfully with a validated dynamic confirmation message
    # And the backend database ledger state must reflect a transaction status of "SUCCESS"

    Examples:
      | RowNumber |
      | 1         |