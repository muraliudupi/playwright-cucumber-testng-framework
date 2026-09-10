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

    private void fillContactFields(String firstNameVal, String lastNameVal, String addressVal,
                                    String cityVal, String stateVal, String zipVal, String phoneVal) {
        firstName().fill(firstNameVal);
        lastName().fill(lastNameVal);
        address().fill(addressVal);
        city().fill(cityVal);
        state().fill(stateVal);
        zip().fill(zipVal);
        phone().fill(phoneVal);
    }

    public WebUpdateContactPage updateContactInfo(UpdateContactData updateContactData) {
        fillContactFields(updateContactData.firstName(), updateContactData.lastName(),
                updateContactData.address().address(), updateContactData.address().city(),
                updateContactData.address().state(), updateContactData.address().zip(),
                updateContactData.phone());
        updateProfileButton().click();
        return this;
    }

    public void clearAllFieldsAndSubmit() {
        fillContactFields("", "", "", "", "", "", "");
        updateProfileButton().click();
    }

    public void verifyContactUpdated() {
        confirmationHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
    }
}
