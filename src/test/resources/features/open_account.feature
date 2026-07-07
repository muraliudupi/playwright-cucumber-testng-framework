Feature: ParaBank Account Lifecycle Management

  @sanity @regression @acctOpn
  Scenario Outline: Open a New Sub-Account and Validate Core Database Persistence
    Given the user is on the ParaBank login page
    When the user logs in using credentials from data key "<TestCaseID>" sheet "AccountOpen"
    And the user navigates to the Open New Account module
    And requests a new "<AccountType>" account using funding account from data key "<TestCaseID>" sheet "AccountOpen"
    Then the system creates the account showing a confirmation page
    # DB Connectivity Issue
    And the backend account ledger table must confirm the new account type is "<AccountType>"

    Examples:
      | TestCaseID    | AccountType |
      | AcctOpen_001  | CHECKING    |
      | AcctOpen_002  | SAVINGS     |