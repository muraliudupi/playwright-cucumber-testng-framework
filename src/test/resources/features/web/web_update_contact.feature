Feature: ParaBank Update Contact Info

  @web @updatecontact
  Scenario: Update contact information successfully
    Given the user is on the ParaBank login page
    When the web user logs in using credentials from data key "Login_001" sheet "LoginData"
    And the user updates contact info using data key "Contact_001" sheet "UpdateContactData"
    Then the contact info update is confirmed