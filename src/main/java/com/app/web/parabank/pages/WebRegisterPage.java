package com.app.web.parabank.pages;

import com.framework.models.RegisterData;
import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

public class WebRegisterPage extends WebBasePage {

    private Locator registerLink() { return page().locator("a:has-text('Register')"); }

    private Locator firstName()        { return page().locator("#customer\\.firstName"); }
    private Locator lastName()         { return page().locator("#customer\\.lastName"); }
    private Locator address()          { return page().locator("#customer\\.address\\.street"); }
    private Locator city()             { return page().locator("#customer\\.address\\.city"); }
    private Locator state()            { return page().locator("#customer\\.address\\.state"); }
    private Locator zip()              { return page().locator("#customer\\.address\\.zipCode"); }
    private Locator phone()            { return page().locator("#customer\\.phoneNumber"); }
    private Locator ssn()              { return page().locator("#customer\\.ssn"); }
    private Locator username()         { return page().locator("#customer\\.username"); }
    private Locator password()         { return page().locator("#customer\\.password"); }
    private Locator repeatedPassword() { return page().locator("#repeatedPassword"); }

    private Locator registerButton()   { return page().locator("input[value='Register']"); }
    private Locator confirmationText() {
        return page().locator("p:has-text('Your account was created successfully. You are now logged in.')");
    }

    private static final String DEFAULT_FIRST_NAME = "Test";
    private static final String DEFAULT_LAST_NAME  = "User";
    private static final String DEFAULT_ADDRESS    = "123 Test St";
    private static final String DEFAULT_CITY       = "Testville";
    private static final String DEFAULT_STATE      = "CA";
    private static final String DEFAULT_ZIP        = "90000";
    private static final String DEFAULT_PHONE      = "1234567890";
    private static final String DEFAULT_SSN        = "123456789";
    private static final String DEFAULT_PASSWORD   = "password1";

    public WebRegisterPage navigateToRegister() {
        registerLink().click();
        waitUntilReady(registerButton());
        return this;
    }

    private void fillCommonRegistrationFields(String firstNameVal, String lastNameVal, String addressVal,
                                               String cityVal, String stateVal, String zipVal,
                                               String phoneVal, String ssnVal) {
        firstName().fill(firstNameVal);
        lastName().fill(lastNameVal);
        address().fill(addressVal);
        city().fill(cityVal);
        state().fill(stateVal);
        zip().fill(zipVal);
        phone().fill(phoneVal);
        ssn().fill(ssnVal);
    }

    private void fillDefaultRegistrationFields(String ssnVal) {
        fillCommonRegistrationFields(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_ADDRESS,
                DEFAULT_CITY, DEFAULT_STATE, DEFAULT_ZIP, DEFAULT_PHONE, ssnVal);
    }

    public void submitNewRegistration(RegisterData registerData, String ssnVal, String usernameVal) {
        fillCommonRegistrationFields(registerData.firstName(), registerData.lastName(),
                registerData.address().address(), registerData.address().city(),
                registerData.address().state(), registerData.address().zip(),
                registerData.phone(), ssnVal);
        username().fill(usernameVal);
        password().fill(registerData.loginDetails().password());
        repeatedPassword().fill(registerData.loginDetails().password());
        registerButton().click();
    }

    public void submitEmptyRegistrationForm() {
        registerButton().click();
    }

    public void submitNewRegistration(String existingUsername) {
        fillDefaultRegistrationFields(DEFAULT_SSN);
        username().fill(existingUsername);
        password().fill(DEFAULT_PASSWORD);
        repeatedPassword().fill(DEFAULT_PASSWORD);
        registerButton().click();
    }

    public void submitMismatchedPasswords(String password1, String password2) {
        fillDefaultRegistrationFields(DEFAULT_SSN);
        username().fill("mismatchtest" + System.currentTimeMillis());
        password().fill(password1);
        repeatedPassword().fill(password2);
        registerButton().click();
    }

    public boolean isRegistrationConfirmed() {
        try {
            confirmationText().waitFor(new Locator.WaitForOptions()
                    .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}