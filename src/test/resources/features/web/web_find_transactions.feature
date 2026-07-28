Feature: ParaBank Find Transactions

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