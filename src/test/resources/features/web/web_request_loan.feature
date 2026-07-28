Feature: ParaBank Request Loan

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