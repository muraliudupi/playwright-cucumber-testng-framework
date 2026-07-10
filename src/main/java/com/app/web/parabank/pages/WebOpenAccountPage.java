package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

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
        accountTypeDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return this;
    }

    public String configureAndOpenAccount(String accountType, String fundingAccount) {
        String sanitizedType = accountType.trim().toUpperCase();

        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));
        boolean requestedAccountFound = true;

        try {
            Locator optionTarget = fromAccountDropdown().locator(String.format("option[value='%s']", fundingAccount));
            optionTarget.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(ConfigReader.getInt("web.dropdown.wait.timeout.ms", 3000)));
            fromAccountDropdown().selectOption(fundingAccount);
        } catch (Exception e) {
            requestedAccountFound = false;
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        String actualFundingAccount = fromAccountDropdown().inputValue();
        if (!requestedAccountFound) {
            LOG.warn("Requested FromAccount '{}' was not available in the dropdown; framework substituted account '{}' instead.",
                    fundingAccount, actualFundingAccount);
        }

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
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 10000)));
    }

    public String getGeneratedAccountId() {
        newAccountIdLink().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return newAccountIdLink().innerText().trim();
    }
}