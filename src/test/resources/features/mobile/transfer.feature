Feature: Mobile Account Funds Transfer

  @sanity @mobile @transfer @wip
  Scenario Outline: Successful fund transfer on mobile
    Given the user is on the mobile login screen
    When the user logs into the mobile app using credentials from data key "<TestCaseID>" sheet "TransferData"
    And the user navigates into the mobile app Transfer Funds interface
    And the user performs a mobile transfer using data from data key "<TestCaseID>" sheet "TransferData"
    Then the mobile app should display a successful transfer message
    # DB Connectivity Issue
    And the backend database ledger state must reflect a transaction status of "SUCCESS"

    Examples:
      | TestCaseID    |
      | Transfer_001  |