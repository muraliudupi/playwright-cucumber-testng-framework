package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;

public class WebOpenAccountPage extends WebBasePage {

    private Locator openNewAccountLink() {
        return page().locator("a:has-text('Open New Account')");
    }

    private Locator accountTypeDropdown() {
        return page().locator("#type");
    }

    private Locator fromAccountDropdown() {
        return page().locator("#fromAccountId");
    }

    private Locator openAccountButton() {
        return page().locator("input[value='Open New Account']");
    }

    private Locator successHeading() {
        return page().locator("#openAccountResult h1.title:has-text('Account Opened!')");
    }

    private Locator newAccountIdLink() {
        return page().locator("#newAccountId");
    }

    public WebOpenAccountPage navigateToOpenAccount() {
        openNewAccountLink().click();
        waitUntilReady(accountTypeDropdown());
        return this;
    }

    public String configureAndOpenAccount(String accountType, String fundingAccount) {
        String sanitizedType = accountType.trim().toUpperCase();

        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));

        String actualFundingAccount = selectAccountWithFallback(fromAccountDropdown(), fundingAccount, 0);

        openAccountButton().click();
        return actualFundingAccount;
    }

/*  Open Account using 1st account in From dropdown.
    public WebOpenAccountPage configureAndOpenAccount(String accountType) {
        String sanitizedType = accountType.trim().toUpperCase();
        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));

        // Since account numbers change constantly, wait for the dropdown to load options
        fromAccountDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));

        // Dynamic Resolution: Instead of catching an error, deliberately select the first option available
        fromAccountDropdown().selectOption(new SelectOption().setIndex(0));

        openAccountButton().click();
        return this;
    }*/

    public void verifyAccountCreationLayoutVisible() {
        successHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
    }

    public String getGeneratedAccountId() {
        newAccountIdLink().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return newAccountIdLink().innerText().trim();
    }
}
