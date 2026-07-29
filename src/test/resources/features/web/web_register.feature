Feature: Registration

  @web @register
  Scenario: Register a new user successfully
    Given the user is on the ParaBank login page
    When the user registers a new user using data key "Register_001" sheet "RegisterData"
    Then the registration is confirmed

  @web @register @negative
  Scenario: Registration fails when username already exists
    Given the user is on the ParaBank login page
    When the user attempts to register with an existing username "john"
    Then the error message "This username already exists." is displayed

  @web @register @negative
  Scenario: Registration fails when submitted with no values
    Given the user is on the ParaBank login page
    When the user submits the registration form without entering any values
    Then the following error messages are displayed:
      | First name is required.             |
      | Last name is required.              |
      | Address is required.                |
      | City is required.                   |
      | State is required.                  |
      | Zip Code is required.               |
      | Social Security Number is required. |
      | Username is required.               |
      | Password is required.               |
      | Password confirmation is required.  |

  @web @register @negative
  Scenario: Registration fails when passwords do not match
    Given the user is on the ParaBank login page
    When the user registers with mismatched passwords "Password123" and "DifferentPass456"
    Then the error message "Passwords did not match." is displayed