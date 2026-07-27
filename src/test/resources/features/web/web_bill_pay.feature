Feature: ParaBank Bill Pay

  @web @billpay
  Scenario: Submit a bill payment successfully
    Given the user is on the ParaBank login page
    When the user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Bill Pay and submits a payment using data key "BillPay_001" sheet "BillPayData"
    Then the bill payment is confirmed