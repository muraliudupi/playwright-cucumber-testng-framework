package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;

public class WebAccountActivityPage extends WebBasePage {

    private Locator accountIdText()   { return page().locator("#accountId"); }
    private Locator errorSection()    { return page().locator("#error"); }

    private Locator monthDropdown()           { return page().locator("#month"); }
    private Locator transactionTypeDropdown() { return page().locator("#transactionType"); }
    private Locator goButton()                { return page().locator("#activityForm input[type='submit']"); }
    private Locator transactionTable()        { return page().locator("#transactionTable"); }
    private Locator noTransactionsMessage()   { return page().locator("#noTransactions"); }
    private Locator transactionRows()         { return transactionTable().locator("tbody tr"); }

    public WebAccountActivityPage waitForAccountDetailsLoaded() {
        int timeoutMs = ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000);
        Locator outcome = page().locator("#accountId:not(:empty), #error:visible");
        outcome.first().waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
        return this;
    }

    public boolean isErrorDisplayed() {
        return errorSection().isVisible();
    }

    public String getDisplayedAccountId() {
        return accountIdText().innerText().trim();
    }

    public WebAccountActivityPage searchActivity(String month, String transactionType) {
        monthDropdown().selectOption(new SelectOption().setLabel(month));
        transactionTypeDropdown().selectOption(new SelectOption().setLabel(transactionType));
        goButton().click();
        awaitActivityOutcome();
        return this;
    }

    private void awaitActivityOutcome() {
        int timeoutMs = ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000);
        Locator outcome = page().locator("#transactionTable:visible, #noTransactions:visible");
        outcome.first().waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
    }

    public boolean hasTransactionResults() {
        return transactionTable().isVisible();
    }

    public int getTransactionRowCount() {
        return hasTransactionResults() ? transactionRows().count() : 0;
    }

    public void clickFirstTransaction() {
        transactionRows().first().locator("a").click();
    }
}