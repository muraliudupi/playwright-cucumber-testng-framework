package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;

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
        page().waitForLoadState(LoadState.NETWORKIDLE);
        firstName().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public WebRegisterPage registerNewUser(String firstNameVal, String lastNameVal, String addressVal, String cityVal,
                                           String stateVal, String zipVal, String phoneVal, String ssnVal,
                                           String usernameVal, String passwordVal) {
        firstName().fill(firstNameVal);
        lastName().fill(lastNameVal);
        address().fill(addressVal);
        city().fill(cityVal);
        state().fill(stateVal);
        zip().fill(zipVal);
        phone().fill(phoneVal);
        ssn().fill(ssnVal);
        username().fill(usernameVal);
        password().fill(passwordVal);
        repeatedPassword().fill(passwordVal);
        registerButton().click();
        page().waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    public void submitEmptyRegistrationForm() {
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
        return confirmationText().isVisible();
    }
}