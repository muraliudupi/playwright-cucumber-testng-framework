Feature: Web - Find Transactions

  @web @findtransactions
  Scenario: A bill payment made today is findable by date
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    And the bill payment is confirmed
    And the user searches transactions for today
    Then the bill payment transaction appears in the results

  @web @findtransactions
  Scenario: A bill payment made today is findable by date range
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    And the bill payment is confirmed
    And the user searches transactions by date range for today
    Then the bill payment transaction appears in the results

  @web @findtransactions
  Scenario: A bill payment made today is findable by amount
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    And the bill payment is confirmed
    And the user searches transactions by amount matching the bill payment
    Then the bill payment transaction appears in the results

  @web @findtransactions
  Scenario: A bill payment made today is findable by its transaction ID
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    And the bill payment is confirmed
    And the user clicks the account number link for the bill payment's funding account
    And the user searches account activity for "All" and "All"
    And the user clicks the transaction link for the bill payment
    And the user notes the transaction ID from the details page
    And the user searches transactions by the noted transaction ID
    Then the bill payment transaction appears in the results

  @web @findtransactions @negative
  Scenario: Find Transactions shows validation errors for each empty search mode
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Find Transactions
    And the user submits each transaction search with no values entered
    Then the following error messages are displayed:
      | Invalid transaction ID |
      | Invalid date format    |
      | Invalid amount         |

  @web @findtransactions @negative
  Scenario: Find Transactions shows validation errors for each invalid search mode
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Find Transactions
    And the user submits each transaction search with invalid values
    Then the following error messages are displayed:
      | Invalid transaction ID |
      | Invalid date format    |
      | Invalid amount         |