package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

public class WebForgotLoginPage extends WebBasePage {

    private Locator forgotLoginLink() { return page().locator("a:has-text('Forgot login info?')"); }

    private Locator firstName() { return page().locator("#firstName"); }
    private Locator lastName()  { return page().locator("#lastName"); }
    private Locator address()   { return page().locator("#address\\.street"); }
    private Locator city()      { return page().locator("#address\\.city"); }
    private Locator state()     { return page().locator("#address\\.state"); }
    private Locator zip()       { return page().locator("#address\\.zipCode"); }
    private Locator ssn()       { return page().locator("#ssn"); }

    private Locator findLoginInfoButton() { return page().locator("input[value='Find My Login Info']"); }
    private Locator confirmationText() {
        return page().locator("p:has-text('Your login information was located successfully. You are now logged in.')");
    }

    public WebForgotLoginPage navigateToForgotLogin() {
        forgotLoginLink().click();
        waitUntilReady(findLoginInfoButton());
        return this;
    }

    public void submitLookup(String firstNameVal, String lastNameVal, String addressVal, String cityVal,
                             String stateVal, String zipVal, String ssnVal) {
        firstName().fill(firstNameVal);
        lastName().fill(lastNameVal);
        address().fill(addressVal);
        city().fill(cityVal);
        state().fill(stateVal);
        zip().fill(zipVal);
        ssn().fill(ssnVal);
        findLoginInfoButton().click();
    }

    public void submitEmptyLookup() {
        findLoginInfoButton().click();
    }

    public boolean isLookupConfirmed() {
        try {
            confirmationText().waitFor(new Locator.WaitForOptions()
                    .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayedCredential(String value) {
        return page().getByText(value).count() > 0;
    }
}