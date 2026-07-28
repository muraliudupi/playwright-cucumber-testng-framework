package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.SelectOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebFindTransactionsPage extends WebBasePage {

    private Locator findTransactionsLink() {
        return page().locator("a:has-text('Find Transactions')");
    }

    private Locator accountDropdown() {
        return page().locator("#accountId");
    }

    private Locator transactionDateInput() {
        return page().locator("#transactionDate");
    }

    private Locator findByDateButton() { return page().locator("#findByDate"); }

    private Locator resultContainer() {
        return page().locator("#resultContainer");
    }

    private Locator errorContainer() {
        return page().locator("#errorContainer");
    }

    public WebFindTransactionsPage navigateToFindTransactions() {
        findTransactionsLink().click();
        accountDropdown().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public WebFindTransactionsPage searchByToday(String account) {
        accountDropdown().selectOption(new SelectOption().setValue(account));

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        transactionDateInput().fill(today);
        findByDateButton().click();

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
        return this;
    }

    public boolean hasResults() {
        return resultContainer().isVisible();
    }

    public boolean resultsContain(String text) {
        return hasResults() && resultContainer()
                .getByText(text, new Locator.GetByTextOptions().setExact(false)).count() > 0;
    }
}