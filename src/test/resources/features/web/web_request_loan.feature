Feature: Request Loan

  @web @requestloan
  Scenario: A small loan request is approved
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user requests a loan using data key "Loan_001" sheet "RequestLoanData"
    Then the loan status matches the expected outcome

  @web @requestloan
  Scenario: A large loan request is denied
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user requests a loan using data key "Loan_002" sheet "RequestLoanData"
    Then the loan status matches the expected outcome

  @web @requestloan @negative
  Scenario: Loan application fails silently with an internal error when submitted empty
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Request Loan and applies without entering any values
    Then the error message "An internal error has occurred and has been logged." is displayed

  @web @requestloan @negative
  Scenario: Loan application fails silently with an internal error when submitted invalid entry
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user navigates to Request Loan and applies by entering invalid values
    Then the error message "An internal error has occurred and has been logged." is displayed