Feature: Web - Unauthenticated Access Guard

  @web @security
  Scenario Outline: Direct navigation to a protected page without a session redirects to the login page
    When the user navigates directly to "<page>" without an active session
    Then the user is redirected to the login page with "An internal error has occurred and has been logged." error

    Examples:
      | page                     |
      | overview.htm             |
      | openaccount.htm          |
      | transfer.htm             |
      | billpay.htm              |
      | findtrans.htm            |
      | updateprofile.htm        |
      | requestloan.htm          |