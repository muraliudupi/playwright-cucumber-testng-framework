package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAccountPage extends BasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

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
        String sanitizedFunding = fundingAccount.trim();

        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));

        fromAccountDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        page().locator("#fromAccountId option").first().waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000)
        );

        java.util.List<String> options = fromAccountDropdown().locator("option").allInnerTexts();

        if (options.contains(sanitizedFunding)) {
            fromAccountDropdown().selectOption(sanitizedFunding);
        } else {
            LOG.warn("Target funding account ID '{}' not found in dropdown list options. Falling back to primary index option.", sanitizedFunding);
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        openAccountButton().click();
        return this;
    }

    public void verifyAccountCreationLayoutVisible() {
        successHeading().waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }

    public String getGeneratedAccountId() {
        newAccountIdLink().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return newAccountIdLink().innerText().trim();
    }
}