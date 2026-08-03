Feature: Web - Accounts Overview Navigation

  @web @accountsoverview
  Scenario: Account number link navigates to Account Details
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user clicks the first account number link on Accounts Overview
    Then the Account Details page is displayed for that account

  @web @accountsoverview
  Scenario: Selecting Account Activity dropdown values shows results
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user clicks the first account number link on Accounts Overview
    And the user searches account activity for "All" and "All"
    Then the account activity results are displayed

  @web @accountsoverview
  Scenario: Clicking a transaction link navigates to Transaction Details
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user clicks the first account number link on Accounts Overview
    And the user searches account activity for "All" and "All"
    And the user clicks the first transaction link
    Then the Transaction Details page is displayed