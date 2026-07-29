package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

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
        waitUntilReady(amountInput());
        return this;
    }

    public TransferAccounts executeTransfer(String amount, String fromAccount, String toAccount) {
        amountInput().fill(amount);

        String actualFrom = selectAccountWithFallback(fromAccountDropdown(), fromAccount, 0);
        String actualTo = selectAccountWithFallback(toAccountDropdown(), toAccount, 1);

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
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
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

    public void executeTransfer(String amount) {
        amountInput().fill(amount);
        transferButton().click();
    }
}
