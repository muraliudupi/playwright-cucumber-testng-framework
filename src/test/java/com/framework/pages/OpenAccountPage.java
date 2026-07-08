package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class OpenAccountPage extends BasePage {

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

    public OpenAccountPage navigateToOpenAccount() {
        openNewAccountLink().click();
        accountTypeDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return this;
    }

    public OpenAccountPage configureAndOpenAccount(String accountType, String fundingAccount) {
        String sanitizedType = accountType.trim().toUpperCase();

        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));

        try {
            Locator optionTarget = fromAccountDropdown().locator(String.format("option[value='%s']", fundingAccount));
            optionTarget.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(3000));
            fromAccountDropdown().selectOption(fundingAccount);
        } catch (Exception e) {
            LOG.warn("Target FromAccount '{}' did not render within timeout. Falling back to index 0.", fundingAccount);
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        openAccountButton().click();
        return this;
    }

/*  Open Account using 1st account in From dropdown.
    public OpenAccountPage configureAndOpenAccount(String accountType) {
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
        successHeading().waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }

    public String getGeneratedAccountId() {
        newAccountIdLink().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return newAccountIdLink().innerText().trim();
    }
}