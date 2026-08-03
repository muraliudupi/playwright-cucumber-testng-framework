Feature: Web - Update Contact Info

  @web @updatecontact
  Scenario: Update contact information successfully
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user updates contact info using data key "Contact_001" sheet "UpdateContactData"
    Then the contact info update is confirmed

  @web @updatecontact @negative
  Scenario: Update Contact shows required-field errors when all fields are cleared
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user clears all contact fields and submits
    Then the following error messages are displayed:
      | First name is required. |
      | Last name is required.  |
      | Address is required.    |
      | City is required.       |
      | State is required.      |
      | Zip Code is required.   |