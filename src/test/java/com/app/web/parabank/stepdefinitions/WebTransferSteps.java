package com.app.web.parabank.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.app.web.parabank.pages.WebTransferPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.Map;

public class WebTransferSteps extends BaseSteps {

    private final WebTransferPage webTransferPage;
    private final ScenarioContext context;

    public WebTransferSteps(WebTransferPage webTransferPage, ScenarioContext context) {
        this.webTransferPage = webTransferPage;
        this.context = context;
    }

    @And("the user navigates to the Transfer Funds interface")
    public void the_user_navigates_to_the_transfer_funds_interface() {
        webTransferPage.navigateToTransferFunds();
    }

    @And("executes a transfer using data from data key {string} sheet {string}")
    public void executes_a_transfer_using_data_from_sheet(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        String amount = rowData.get("Amount");
        String fromAccount = rowData.get("FromAccount");
        String toAccount = rowData.get("ToAccount");

        WebTransferPage.TransferAccounts actualAccounts = webTransferPage.executeTransfer(amount, fromAccount, toAccount);


        context.setContext("TX_AMOUNT", amount);
        context.setContext("TX_FROM", actualAccounts.actualFromAccount());
        context.setContext("TX_TO", actualAccounts.actualToAccount());

        if (!actualAccounts.actualFromAccount().equals(fromAccount) || !actualAccounts.actualToAccount().equals(toAccount)) {
            LOG.warn("Test data requested From={}, To={} but framework substituted From={}, To={}.",
                    fromAccount, toAccount, actualAccounts.actualFromAccount(), actualAccounts.actualToAccount());
        }

/*      Gets first From and To account and perform transfer.
        com.microsoft.playwright.Page p = com.framework.core.WebDriverFactory.getPage();

        p.locator("#fromAccountId option").first().waitFor();

        java.util.List<String> options = p.locator("#fromAccountId option").allInnerTexts();
        if (options.size() < 2) {
            throw new IllegalStateException("Automation Failure: Insufficient dynamic accounts found in UI dropdown to perform a transfer.");
        }

        String dynamicFromAccount = options.get(0).trim();
        String dynamicToAccount = options.get(1).trim();

        context.setContext("TX_AMOUNT", amount);
        context.setContext("TX_FROM", dynamicFromAccount);
        context.setContext("TX_TO", dynamicToAccount);

        // Execute step cleanly with deterministic state values
        webTransferPage.executeTransfer(amount, dynamicFromAccount, dynamicToAccount);*/
    }

    @Then("the transfer completes successfully with a validated dynamic confirmation message")
    public void the_transfer_completes_successfully_with_a_validated_dynamic_confirmation_message() {
        webTransferPage.verifyTransferLayoutVisible();
        String expectedAmount = context.getStringContext("TX_AMOUNT");

        boolean isValid = webTransferPage.isResultMessageValidFor(expectedAmount);
        String actualMessage = webTransferPage.getActualResultMessage();


        org.testng.Assert.assertTrue(
                isValid,
                String.format("Format Mismatch!\nActual: '%s'\nExpected Amount: '%s'", actualMessage, expectedAmount)
        );
    }

    @And("the backend database ledger state must reflect a transaction status of {string}")
    public void the_backend_database_ledger_state_must_reflect_a_transaction_status_of(String expectedDbStatus) {
        boolean isDbValidationActive = Boolean.parseBoolean(ConfigReader.get("db.validation.enabled"));
        if (!isDbValidationActive) {
            LOG.warn("Database Audit Warning: 'db.validation.enabled' is false. Skipping transaction verification step.");
            return;
        }

        String sqlQuery = "SELECT transaction_status FROM bank_ledger " +
                "WHERE from_account = ? AND to_account = ? AND amount = ? " +
                "ORDER BY timestamp DESC LIMIT 1";

        String expectedAmount = context.getStringContext("TX_AMOUNT");
        String expectedFrom = context.getStringContext("TX_FROM");
        String expectedTo = context.getStringContext("TX_TO"); // Bounded context value

        String sanitizedAmountStr = expectedAmount.replaceAll("[\\$, ]", "").trim();
        if (sanitizedAmountStr.isEmpty()) {
            org.testng.Assert.fail("AUTOMATION ERROR: Context execution transaction evaluate string parameter returned blank value.");
        }

        java.math.BigDecimal amountForQuery = new java.math.BigDecimal(sanitizedAmountStr).setScale(2, java.math.RoundingMode.HALF_UP);

        String actualDbStatus = DatabaseUtil.getSingleValueWithRetry(
                ConfigReader.getInt("db.retry.max.timeout.sec", 5),
                ConfigReader.getInt("db.retry.poll.interval.ms", 500),
                sqlQuery, "transaction_status", expectedFrom, expectedTo, amountForQuery);

        org.testng.Assert.assertEquals(actualDbStatus, expectedDbStatus,
                String.format("CRITICAL DESYNC FAILURE: Thread clashing or missing ledger row! " +
                                "UI displayed success, but DB ledger transaction status resolved to: '%s' for transfer from %s to %s.",
                        actualDbStatus, expectedFrom, expectedTo));

        LOG.info("[Thread-{}] Database verification success: Status verified as {} for transfer from {} to {} of amount [{}]",
                Thread.currentThread().threadId(), actualDbStatus, expectedFrom, expectedTo, amountForQuery);
    }
}