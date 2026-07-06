package com.framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TransferPage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(TransferPage.class);

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

        fromAccountDropdown().waitFor(new Locator.WaitForOptions().setTimeout(5000));
        page().locator("#fromAccountId option").first().waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(5000)
        );

        List<String> elementValues = fromAccountDropdown().locator("option").allInnerTexts();

        if (elementValues.contains(fromAccount)) {
            fromAccountDropdown().selectOption(fromAccount);
        } else {
            LOG.warn("Excel FromAccount '{}' missing from system options. Instantly defaulting to index 0.", fromAccount);
            fromAccountDropdown().selectOption(new SelectOption().setIndex(0));
        }

        if (elementValues.contains(toAccount)) {
            toAccountDropdown().selectOption(toAccount);
        } else {
            LOG.warn("Excel ToAccount '{}' missing from system options. Instantly defaulting to index 1.", toAccount);
            toAccountDropdown().selectOption(new SelectOption().setIndex(1));
        }

        transferButton().click();
    }

    public void verifyTransferLayoutVisible() {
        transferSuccessHeading().waitFor(new Locator.WaitForOptions().setTimeout(10000));
    }

    public String getActualResultMessage() {
        return transferResultMessage().innerText().trim();
    }
}