package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

public class WebUpdateContactPage extends WebBasePage {

    private Locator updateContactLink() { return page().locator("a:has-text('Update Contact Info')"); }
    private Locator firstName() { return page().locator("#customer\\.firstName"); }
    private Locator lastName()  { return page().locator("#customer\\.lastName"); }
    private Locator address()   { return page().locator("#customer\\.address\\.street"); }
    private Locator city()      { return page().locator("#customer\\.address\\.city"); }
    private Locator state()     { return page().locator("#customer\\.address\\.state"); }
    private Locator zip()       { return page().locator("#customer\\.address\\.zipCode"); }
    private Locator phone()     { return page().locator("#customer\\.phoneNumber"); }
    private Locator updateProfileButton() { return page().locator("input[value='Update Profile']"); }
    private Locator confirmationHeading() { return page().locator("#updateProfileResult h1.title:has-text('Profile Updated')"); }

    public WebUpdateContactPage navigateToUpdateContact() {
        updateContactLink().click();
        firstName().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public WebUpdateContactPage updateContactInfo(String first, String last, String addr, String cityVal,
                                                  String stateVal, String zipVal, String phoneVal) {
        firstName().fill(first);
        lastName().fill(last);
        address().fill(addr);
        city().fill(cityVal);
        state().fill(stateVal);
        zip().fill(zipVal);
        phone().fill(phoneVal);
        updateProfileButton().click();
        return this;
    }

    public void verifyContactUpdated() {
        confirmationHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
    }
}