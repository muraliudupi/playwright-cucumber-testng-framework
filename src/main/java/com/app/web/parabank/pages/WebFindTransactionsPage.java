package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebFindTransactionsPage extends WebBasePage {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private Locator findTransactionsLink() { return page().locator("a:has-text('Find Transactions')"); }
    private Locator accountDropdown()      { return page().locator("#accountId"); }

    private Locator transactionID() { return page().locator("#transactionId"); }
    private Locator findByIdButton() { return page().locator("#findById"); }

    private Locator transactionDateInput() { return page().locator("#transactionDate"); }
    private Locator findByDateButton()     { return page().locator("#findByDate"); }

    private Locator dateRangeFromInput()      { return page().locator("#fromDate"); }
    private Locator dateRangeToInput()        { return page().locator("#toDate"); }
    private Locator findByDateRangeButton()   { return page().locator("#findByDateRange"); }

    private Locator amountInput()          { return page().locator("#amount"); }
    private Locator findByAmountButton()   { return page().locator("#findByAmount"); }

    private Locator resultContainer() { return page().locator("#resultContainer"); }
    private Locator errorContainer()  { return page().locator("#errorContainer"); }

    public void submitFindByTransactionIdWithoutValue() { findByIdButton().click(); }
    public void submitFindByDateWithoutValue()          { findByDateButton().click(); }
    public void submitFindByDateRangeWithoutValues()    { findByDateRangeButton().click(); }
    public void submitFindByAmountWithoutValue()        { findByAmountButton().click(); }

    public void submitFindByTransactionIdWithInvalidValue() {
        transactionID().fill("#$%^&*(");
        findByIdButton().click();
    }
    public void submitFindByDateWithInvalidValue() {
        transactionDateInput().fill("#$%^&*(");
        findByDateButton().click();
    }
    public void submitFindByDateRangeWithInvalidValue() {
        dateRangeFromInput().fill("#$%^&*(");
        dateRangeToInput().fill("#$%^&*(");
        findByDateRangeButton().click();
    }
    public void submitFindByAmountWithInvalidValue() {
        amountInput().fill("#$%^&*(");
        findByAmountButton().click();
    }

    public WebFindTransactionsPage navigateToFindTransactions() {
        findTransactionsLink().click();
        waitUntilReady(findByAmountButton());
        return this;
    }

    public WebFindTransactionsPage searchByToday(String account) {
        selectAccount(account);
        String today = LocalDate.now().format(DATE_FORMAT);
        transactionDateInput().fill(today);
        findByDateButton().click();
        awaitSearchOutcome();
        return this;
    }

    public WebFindTransactionsPage searchByDateRange(String account, LocalDate from, LocalDate to) {
        selectAccount(account);
        dateRangeFromInput().fill(from.format(DATE_FORMAT));
        dateRangeToInput().fill(to.format(DATE_FORMAT));
        findByDateRangeButton().click();
        awaitSearchOutcome();
        return this;
    }

    public WebFindTransactionsPage searchByAmount(String account, String amount) {
        selectAccount(account);
        amountInput().fill(amount);
        findByAmountButton().click();
        awaitSearchOutcome();
        return this;
    }

    private void selectAccount(String account) {
        accountDropdown().selectOption(new SelectOption().setValue(account));
    }

    private void awaitSearchOutcome() {
        int timeoutMs = ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000);
        Locator outcome = page().locator("#resultContainer:visible, #errorContainer:visible");
        outcome.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));

        if (errorContainer().isVisible()) {
            LOG.error("Find Transactions returned an application error: {}", errorContainer().innerText().trim());
        }
    }

    public boolean hasResults() {
        return resultContainer().isVisible();
    }

    public boolean resultsContain(String text) {
        return hasResults() && resultContainer()
                .getByText(text, new Locator.GetByTextOptions().setExact(false)).count() > 0;
    }
}
