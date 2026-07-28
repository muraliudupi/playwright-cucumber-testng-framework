Feature: ParaBank Bill Pay

  @web @billpay
  Scenario: Submit a bill payment successfully
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    Then the bill payment is confirmed


  @web @billpay @negative
  Scenario: Bill Pay shows required-field errors when submitted empty
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits without entering any values
    Then the following error messages are displayed:
      | Payee name is required.        |
      | Address is required.           |
      | City is required.              |
      | State is required.             |
      | Zip Code is required.          |
      | Phone number is required.      |
      | Account number is required.    |
      | The amount cannot be empty.    |

  @web @billpay @negative
  Scenario: Bill Pay shows required-field errors when submitted invalid entries
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits with invalid entering in Account & Amount fields
    Then the following error messages are displayed:
      | Please enter a valid number.   |
      | Please enter a valid amount.   |
