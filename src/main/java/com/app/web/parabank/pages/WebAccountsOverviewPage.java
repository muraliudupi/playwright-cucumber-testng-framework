package com.app.web.parabank.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import java.math.BigDecimal;
import java.util.List;

public class WebAccountsOverviewPage extends WebBasePage {

    public record AccountBalance(String accountNumber, BigDecimal availableAmount) {
    }

    private Locator accountsOverviewLink() {
        return page().locator("a:has-text('Accounts Overview')");
    }

    private Locator accountTable() {
        return page().locator("#accountTable");
    }

    private Locator accountRows() {
        return accountTable().locator("tbody tr");
    }

    public WebAccountsOverviewPage navigateToAccountsOverview() {
        accountsOverviewLink().click();
        page().waitForLoadState(LoadState.NETWORKIDLE);
        accountTable().waitFor(new Locator.WaitForOptions()
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
        return this;
    }

    public AccountBalance findAccountWithHighestAvailableAmount() {
        int rowCount = accountRows().count();
        String bestAccount = null;
        BigDecimal bestAmount = BigDecimal.valueOf(-1);

        for (int i = 0; i < rowCount; i++) {
            Locator row = accountRows().nth(i);
            Locator accountLink = row.locator("a[href*='activity.htm']");
            if (accountLink.count() == 0) {
                continue;
            }

            List<String> cells = row.locator("td").allInnerTexts();
            if (cells.size() < 3) {
                continue;
            }

            String accountNumber = accountLink.innerText().trim();
            BigDecimal availableAmount = parseCurrency(cells.get(2));

            if (availableAmount.compareTo(bestAmount) > 0) {
                bestAmount = availableAmount;
                bestAccount = accountNumber;
            }
        }

        if (bestAccount == null) {
            throw new IllegalStateException("No usable account rows found on Accounts Overview.");
        }

        LOG.info("Accounts Overview: highest available balance found on account '{}' with ${}.", bestAccount, bestAmount);
        return new AccountBalance(bestAccount, bestAmount);
    }

    private BigDecimal parseCurrency(String text) {
        String cleaned = text.replaceAll("[^0-9.\\-]", "");
        return cleaned.isEmpty() ? BigDecimal.ZERO : new BigDecimal(cleaned);
    }
}