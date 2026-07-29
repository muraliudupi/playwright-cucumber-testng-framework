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

    public BigDecimal calculateTotalBalanceAmount() {
        int rowCount = accountRows().count();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int processedAccountsCount = 0;

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

            BigDecimal balance = parseCurrency(cells.get(1));
            totalAmount = totalAmount.add(balance);
            processedAccountsCount++;
        }

        if (processedAccountsCount == 0) {
            throw new IllegalStateException("No usable account rows found on Accounts Overview.");
        }

        LOG.info("Accounts Overview: total account Balance across {} accounts is - ${}.", processedAccountsCount, totalAmount);
        return totalAmount;
    }


    private BigDecimal parseCurrency(String text) {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String trimmed = text.trim();

        // Detect negative formats: ($100.00), -$100.00, $100.00-, or unicode minus (− / –)
        boolean isNegative = (trimmed.startsWith("(") && trimmed.endsWith(")"))
                || trimmed.contains("-")
                || trimmed.contains("−")
                || trimmed.contains("–");

        // Strip everything except digits and decimal point
        String cleaned = trimmed.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal amount = new BigDecimal(cleaned);
        return isNegative ? amount.negate() : amount;
    }

    public AccountBalance findAccountWithHighestAvailableAmount() {
        int rowCount = accountRows().count();
        String bestAccount = null;
        BigDecimal bestAmount = null;

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

            if (bestAmount == null || availableAmount.compareTo(bestAmount) > 0) {
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

    public BigDecimal calculateTotalAvailableAmount() {
        int rowCount = accountRows().count();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int processedAccountsCount = 0;

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

            BigDecimal availableAmount = parseCurrency(cells.get(2));
            totalAmount = totalAmount.add(availableAmount);
            processedAccountsCount++;
        }

        if (processedAccountsCount == 0) {
            throw new IllegalStateException("No usable account rows found on Accounts Overview.");
        }

        LOG.info("Accounts Overview: total available balance across {} accounts is - ${}.", processedAccountsCount, totalAmount);
        return totalAmount;
    }
}