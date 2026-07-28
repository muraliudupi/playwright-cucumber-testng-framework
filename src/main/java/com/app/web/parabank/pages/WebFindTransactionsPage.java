package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebFindTransactionsPage extends WebBasePage {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private Locator findTransactionsLink() { return page().locator("a:has-text('Find Transactions')"); }
    private Locator accountDropdown()      { return page().locator("#accountId"); }

    private Locator transactionDateInput() { return page().locator("#transactionDate"); }
    private Locator findByDateButton()     { return page().locator("#findByDate"); }

    private Locator dateRangeFromInput()      { return page().locator("#fromDate"); }
    private Locator dateRangeToInput()        { return page().locator("#toDate"); }
    private Locator findByDateRangeButton()   { return page().locator("#findByDateRange"); }

    private Locator amountInput()          { return page().locator("#amount"); }
    private Locator findByAmountButton()   { return page().locator("#findByAmount"); }

    private Locator resultContainer() { return page().locator("#resultContainer"); }
    private Locator errorContainer()  { return page().locator("#errorContainer"); }

    public WebFindTransactionsPage navigateToFindTransactions() {
        findTransactionsLink().click();
        page().waitForLoadState(LoadState.NETWORKIDLE);
        accountDropdown().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
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
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (errorContainer().isVisible() || resultContainer().isVisible()) {
                break;
            }
            page().waitForTimeout(200);
        }
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