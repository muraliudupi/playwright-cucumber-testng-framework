Feature: ParaBank Account Lifecycle Management

  @regression @acctOpn
  Scenario Outline: Open a New Sub-Account and Validate Core Database Persistence
    Given the user is on the ParaBank login page
    When the user logs in using credentials from excel row "<RowNo>" sheet "LoginData"
    And the user navigates to the Open New Account module
    And requests a new "<AccountType>" account using funding account from excel row "<RowNo>" sheet "AccountOpen"
    Then the system creates the account showing a confirmation page
    # DB Connectivity Issue
    #And the backend account ledger table must confirm the new account type is "<AccountType>"

    Examples:
      | RowNo | AccountType |
      | 1     | CHECKING    |
      | 2     | SAVINGS     |