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

    public WebRegisterPage navigateToRegister() {
        registerLink().click();
        waitUntilReady(registerButton());
        return this;
    }

    public void submitNewRegistration(RegisterData registerData, String ssnVal, String usernameVal) {
        firstName().fill(registerData.firstName());
        lastName().fill(registerData.lastName());
        address().fill(registerData.address().address());
        city().fill(registerData.address().city());
        state().fill(registerData.address().state());
        zip().fill(registerData.address().zip());
        phone().fill(registerData.phone());
        ssn().fill(ssnVal);
        username().fill(usernameVal);
        password().fill(registerData.loginDetails().password());
        repeatedPassword().fill(registerData.loginDetails().password());
        registerButton().click();
    }

    public void submitEmptyRegistrationForm() {
        registerButton().click();
    }

    public void submitNewRegistration(String existingUsername) {
        firstName().fill("Test");
        lastName().fill("User");
        address().fill("123 Test St");
        city().fill("Testville");
        state().fill("CA");
        zip().fill("90000");
        phone().fill("1234567890");
        ssn().fill("123456789");
        username().fill(existingUsername);
        password().fill("password1");
        repeatedPassword().fill("password1");
        registerButton().click();
    }

    public void submitMismatchedPasswords(String password1, String password2) {
        firstName().fill("Test");
        lastName().fill("User");
        address().fill("123 Test St");
        city().fill("Testville");
        state().fill("CA");
        zip().fill("90000");
        phone().fill("1234567890");
        ssn().fill("123456789");
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