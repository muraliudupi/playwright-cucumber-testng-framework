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
        accountTypeDropdown().selectOption(accountType.toUpperCase());

        fromAccountDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        page().locator("#fromAccountId option").first().waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000)
        );

        java.util.List<String> options = fromAccountDropdown().locator("option").allInnerTexts();
        if (options.contains(fundingAccount)) {
            fromAccountDropdown().selectOption(fundingAccount);
        } else {
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