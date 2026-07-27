Feature: ParaBank Request Loan

  @web @requestloan
  Scenario: Submit a loan request and receive a decision
    Given the user is on the ParaBank login page
    When the user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user requests a loan using data key "Loan_001" sheet "RequestLoanData"
    Then a loan status decision is displayed