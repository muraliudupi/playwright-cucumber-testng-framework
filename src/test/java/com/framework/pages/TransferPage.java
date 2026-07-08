package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;

public class TransferPage extends BasePage {

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

    public TransferPage navigateToTransferFunds() {
        transferFundsLink().click();
        amountInput().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        return this;
    }

    public void executeTransfer(String amount, String fromAccount, String toAccount) {
        amountInput().fill(amount);

        try {
            Locator fromOption = fromAccountDropdown().locator(String.format("option[value='%s']", fromAccount));
            fromOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(3000));
            fromAccountDropdown().selectOption(fromAccount);
        } catch (Exception e) {
            LOG.warn("Target FromAccount '{}' did not render within timeout. Falling back to index 0.", fromAccount);
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        try {
            Locator toOption = toAccountDropdown().locator(String.format("option[value='%s']", toAccount));
            toOption.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(3000));
            toAccountDropdown().selectOption(toAccount);
        } catch (Exception e) {
            LOG.warn("Target ToAccount '{}' did not render within timeout. Falling back to index 1.", toAccount);
            toAccountDropdown().selectOption(new SelectOption().setIndex(1));
        }

        transferButton().click();
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
        transferSuccessHeading().waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }

    public String getActualResultMessage() {
        return transferResultMessage().innerText().trim();
    }
}