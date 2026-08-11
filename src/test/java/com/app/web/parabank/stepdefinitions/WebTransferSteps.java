package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebTransferPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.models.TransferData;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

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
        TransferData transferData = getExcelModelByKey(testCaseId, sheetName, TransferData::fromMap);

        String amount = String.valueOf(transferData.amount());
        String fromAccount = transferData.fromAccount();
        String toAccount = transferData.toAccount();

        WebTransferPage.TransferAccounts actualAccounts = webTransferPage.executeTransfer(transferData);

        context.setContext(ContextKeys.TX_AMOUNT, amount);
        context.setContext(ContextKeys.TX_FROM, actualAccounts.actualFromAccount());
        context.setContext(ContextKeys.TX_TO, actualAccounts.actualToAccount());

        if (!actualAccounts.actualFromAccount().equals(fromAccount) || !actualAccounts.actualToAccount().equals(toAccount)) {
            LOG.warn("Test data requested From={}, To={} but framework substituted From={}, To={}.",
                    fromAccount, toAccount, actualAccounts.actualFromAccount(), actualAccounts.actualToAccount());
        }
    }

    @Then("the transfer completes successfully with a validated dynamic confirmation message")
    public void the_transfer_completes_successfully_with_a_validated_dynamic_confirmation_message() {
        webTransferPage.verifyTransferLayoutVisible();
        String expectedAmount = context.getStringContext(ContextKeys.TX_AMOUNT);

        boolean isValid = webTransferPage.isResultMessageValidFor(expectedAmount);
        String actualMessage = webTransferPage.getActualResultMessage();

        assertTrue(
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

        String expectedAmount = context.getStringContext(ContextKeys.TX_AMOUNT);
        String expectedFrom = context.getStringContext(ContextKeys.TX_FROM);
        String expectedTo = context.getStringContext(ContextKeys.TX_TO);

        String sanitizedAmountStr = expectedAmount.replaceAll("[\\$, ]", "").trim();
        if (sanitizedAmountStr.isEmpty()) {
            fail("AUTOMATION ERROR: Context execution transaction evaluate string parameter returned blank value.");
        }

        java.math.BigDecimal amountForQuery = new java.math.BigDecimal(sanitizedAmountStr).setScale(2, java.math.RoundingMode.HALF_UP);

        String actualDbStatus = DatabaseUtil.getSingleValueWithRetry(
                sqlQuery, "transaction_status", expectedFrom, expectedTo, amountForQuery);

        assertEquals(actualDbStatus, expectedDbStatus,
                String.format("CRITICAL DESYNC FAILURE: Thread clashing or missing ledger row! " +
                                "UI displayed success, but DB ledger transaction status resolved to: '%s' for transfer from %s to %s.",
                        actualDbStatus, expectedFrom, expectedTo));

        LOG.info("[Thread-{}] Database verification success: Status verified as {} for transfer from {} to {} of amount [{}]",
                Thread.currentThread().threadId(), actualDbStatus, expectedFrom, expectedTo, amountForQuery);
    }

    @And("the user attempts a transfer with a missing amount")
    public void the_user_attempts_transfer_with_missing_amount() {
        webTransferPage.navigateToTransferFunds()
                .executeTransfer("");
    }

    @And("the user attempts a transfer with an invalid amount")
    public void the_user_attempts_transfer_with_invalid_amount() {
        webTransferPage.navigateToTransferFunds()
                .executeTransfer("#$%^");
    }
}