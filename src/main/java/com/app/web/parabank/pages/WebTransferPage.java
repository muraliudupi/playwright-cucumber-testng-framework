package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class WebTransferPage extends WebBasePage {

    private Locator transferFundsLink() {
        return page().locator("a:has-text('Transfer Funds')");
    }

    private Locator amountInput() {
        return page().locator("#amount");
    }

    private Locator fromAccountDropdown() {
        return page().locator("#fromAccountId");
    }

    private Locator toAccountDropdown() {
        return page().locator("#toAccountId");
    }

    private Locator transferButton() {
        return page().locator("input[value='Transfer']");
    }

    private Locator transferSuccessHeading() {
        return page().locator("#showResult h1.title:has-text('Transfer Complete!')");
    }

    private Locator transferResultMessage() {
        return page().locator("#showResult p").first();
    }

    public record TransferAccounts(String actualFromAccount, String actualToAccount) {
    }

    public WebTransferPage navigateToTransferFunds() {
        transferFundsLink().click();
        amountInput().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public TransferAccounts executeTransfer(String amount, String fromAccount, String toAccount) {
        amountInput().fill(amount);
        int dropdownTimeout = ConfigReader.getInt("web.dropdown.wait.timeout.ms", 3000);

        boolean fromFound = true;
        boolean toFound = true;

        try {
            Locator fromOption = fromAccountDropdown().locator(String.format("option[value='%s']", fromAccount));
            fromOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(dropdownTimeout));
            fromAccountDropdown().selectOption(fromAccount);
        } catch (Exception e) {
            fromFound = false;
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        try {
            Locator toOption = toAccountDropdown().locator(String.format("option[value='%s']", toAccount));
            toOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(dropdownTimeout));
            toAccountDropdown().selectOption(toAccount);
        } catch (Exception e) {
            toFound = false;
            toAccountDropdown().selectOption(new SelectOption().setIndex(1));
        }

        String actualFrom = fromAccountDropdown().inputValue();
        String actualTo = toAccountDropdown().inputValue();

        if (!fromFound) {
            LOG.warn("Requested FromAccount '{}' unavailable; framework substituted '{}'.", fromAccount, actualFrom);
        }
        if (!toFound) {
            LOG.warn("Requested ToAccount '{}' unavailable; framework substituted '{}'.", toAccount, actualTo);
        }

        transferButton().click();
        return new TransferAccounts(actualFrom, actualTo);

    }

/*  Transfer using 1st account in From & To dropdown.
    public void executeTransfer(String amount, String fromAccount, String toAccount) {
        amountInput().fill(amount);

        Locator fromOption = fromAccountDropdown().locator(String.format("option[value='%s']", fromAccount));
        fromOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
        fromAccountDropdown().selectOption(fromAccount);

        Locator toOption = toAccountDropdown().locator(String.format("option[value='%s']", toAccount));
        toOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000));
        toAccountDropdown().selectOption(toAccount);

        transferButton().click();
    }*/

    public void verifyTransferLayoutVisible() {
        transferSuccessHeading().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 10000)));
    }

    public String getActualResultMessage() {
        return transferResultMessage().innerText().trim();
    }

    public boolean isResultMessageValidFor(String expectedAmount) {
        String normalizedAmount = expectedAmount.startsWith("$")
                ? expectedAmount
                : "$" + String.format("%.2f", Double.parseDouble(expectedAmount));
        String regexAmount = normalizedAmount.replace("$", "\\$");
        String validationRegex = "^" + regexAmount + " has been transferred from account #(\\d+) to account #(\\d+)\\.$";

        return getActualResultMessage().matches(validationRegex);
    }
}