package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WebFindTransactionsPage extends WebBasePage {

    private Locator findTransactionsLink() { return page().locator("a:has-text('Find Transactions')"); }
    private Locator transactionDateInput() { return page().locator("#transactionDate"); }
    private Locator findByDateButton()     { return page().locator("#findByDate"); }
    private Locator resultsTable()         { return page().locator("#transactionResults table"); }

    public WebFindTransactionsPage navigateToFindTransactions() {
        findTransactionsLink().click();
        transactionDateInput().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public WebFindTransactionsPage searchByToday() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        transactionDateInput().fill(today);
        findByDateButton().click();
        resultsTable().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 10000)));
        return this;
    }

    public boolean resultsContain(String text) {
        return resultsTable().getByText(text, new Locator.GetByTextOptions().setExact(false)).count() > 0;
    }
}