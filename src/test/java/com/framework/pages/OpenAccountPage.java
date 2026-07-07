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

        accountTypeDropdown().selectOption(new SelectOption().setLabel(sanitizedType));

        Locator optionTarget = fromAccountDropdown().locator("option");
        optionTarget.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));

        try {
            Locator specificOption = fromAccountDropdown().locator(String.format("option[value='%s']", fundingAccount));

            if (specificOption.count() > 0) {
                fromAccountDropdown().selectOption(fundingAccount);
                LOG.info("Successfully matched and selected funding account option: {}", fundingAccount);
            } else {
                LOG.warn("Target funding account ID '{}' not present in UI select element. Falling back to primary index option.", fundingAccount);
                fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
            }
        } catch (Exception e) {
            LOG.error("Dropdown evaluation phase threw an unexpected anomaly. Enforcing structural fallback to index zero.", e);
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