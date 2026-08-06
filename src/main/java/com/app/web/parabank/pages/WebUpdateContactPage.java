package com.app.web.parabank.pages;

import com.framework.models.UpdateContactData;
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
        waitUntilReady(updateProfileButton());
        return this;
    }

    public WebUpdateContactPage updateContactInfo(UpdateContactData updateContactData) {
        firstName().fill(updateContactData.firstName());
        lastName().fill(updateContactData.lastName());
        address().fill(updateContactData.address().address());
        city().fill(updateContactData.address().city());
        state().fill(updateContactData.address().state());
        zip().fill(updateContactData.address().zip());
        phone().fill(updateContactData.phone());
        updateProfileButton().click();
        return this;
    }

    public void clearAllFieldsAndSubmit() {
        firstName().fill("");
        lastName().fill("");
        address().fill("");
        city().fill("");
        state().fill("");
        zip().fill("");
        phone().fill("");
        updateProfileButton().click();
    }

    public void verifyContactUpdated() {
        confirmationHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
    }
}
