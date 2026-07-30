Feature: Forgot Login Info

  @web @register @forgotlogin
  Scenario: Look up login info for a newly registered user
    Given the user is on the ParaBank login page
    When the user registers a new user using data key "Register_001" sheet "RegisterData"
    And the registration is confirmed
    And the user logs out
    And the user is returned to the login page
    And the user looks up login info using the registered user's details
    Then the login info lookup is confirmed and shows the registered credentials

  @web @forgotlogin @negative
  Scenario: Login info lookup fails when submitted with no values
    Given the user is on the ParaBank login page
    When the user submits the login info lookup without entering any values
    Then the following error messages are displayed:
      | First name is required.             |
      | Last name is required.              |
      | Address is required.                |
      | City is required.                   |
      | State is required.                  |
      | Zip Code is required.               |
      | Social Security Number is required. |

  @web @forgotlogin @negative
  Scenario: Login info lookup fails when details don't match any customer
    Given the user is on the ParaBank login page
    When the user looks up login info with details that do not match any customer
    Then the error message "The customer information provided could not be found." is displayed