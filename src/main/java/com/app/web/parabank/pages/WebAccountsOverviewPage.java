package com.app.web.parabank.pages;

import com.microsoft.playwright.Locator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WebAccountsOverviewPage extends WebBasePage {

    public record AccountBalance(String accountNumber, BigDecimal availableAmount) {
    }

    private record AccountRow(String accountNumber, BigDecimal balance, BigDecimal availableAmount) {
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
        waitUntilReady(accountTable());
        return this;
    }

    public String clickFirstAccountNumberLink() {
        Locator firstLink = accountRows().first().locator("a[href*='activity.htm']");
        String accountNumber = firstLink.innerText().trim();
        firstLink.click();
        return accountNumber;
    }

    public BigDecimal calculateTotalBalanceAmount() {
        List<AccountRow> accounts = readUsableAccounts();
        BigDecimal totalAmount = accounts.stream()
                .map(AccountRow::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOG.info("Accounts Overview: total account Balance across {} accounts is - ${}.", accounts.size(), totalAmount);
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
        AccountRow highestAvailableAccount = readUsableAccounts().stream()
                .max(java.util.Comparator.comparing(AccountRow::availableAmount))
                .orElseThrow(() -> new IllegalStateException("No usable account rows found on Accounts Overview."));

        LOG.info("Accounts Overview: highest available balance found on account '{}' with ${}.",
                highestAvailableAccount.accountNumber(), highestAvailableAccount.availableAmount());
        return new AccountBalance(highestAvailableAccount.accountNumber(), highestAvailableAccount.availableAmount());
    }

    public BigDecimal calculateTotalAvailableAmount() {
        List<AccountRow> accounts = readUsableAccounts();
        BigDecimal totalAmount = accounts.stream()
                .map(AccountRow::availableAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LOG.info("Accounts Overview: total available balance across {} accounts is - ${}.", accounts.size(), totalAmount);
        return totalAmount;
    }

    private List<AccountRow> readUsableAccounts() {
        int rowCount = accountRows().count();
        List<AccountRow> accounts = new ArrayList<>();

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

            accounts.add(new AccountRow(
                    accountLink.innerText().trim(),
                    parseCurrency(cells.get(1)),
                    parseCurrency(cells.get(2))));
        }

        if (accounts.isEmpty()) {
            throw new IllegalStateException("No usable account rows found on Accounts Overview.");
        }
        return accounts;
    }
}
