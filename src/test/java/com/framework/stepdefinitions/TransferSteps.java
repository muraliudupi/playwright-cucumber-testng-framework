package com.framework.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.framework.pages.TransferPage;
import com.framework.utils.ConfigReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransferSteps extends BaseSteps {

    private final TransferPage transferPage;
    private final ScenarioContext context;

    public TransferSteps(TransferPage transferPage, ScenarioContext context) {
        this.transferPage = transferPage;
        this.context = context;
    }

    @And("the user navigates to the Transfer Funds interface")
    public void the_user_navigates_to_the_transfer_funds_interface() {
        transferPage.navigateToTransferFunds();
    }

    @And("executes a transfer using data from data key {string} sheet {string}")
    public void executes_a_transfer_using_data_from_excel_row_sheet(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        String amount = rowData.get("Amount");
        String fromAccount = rowData.get("FromAccount");
        String toAccount = rowData.get("ToAccount");

        context.setContext("TX_AMOUNT", amount);
        context.setContext("TX_FROM", fromAccount);

        transferPage.executeTransfer(amount, fromAccount, toAccount);
    }

    @Then("the transfer completes successfully with a validated dynamic confirmation message")
    public void the_transfer_completes_successfully_with_a_validated_dynamic_confirmation_message() {
        transferPage.verifyTransferLayoutVisible();
        String expectedAmount = context.getStringContext("TX_AMOUNT");
        String actualMessage = transferPage.getActualResultMessage();

        if (!expectedAmount.startsWith("$")) {
            expectedAmount = "$" + String.format("%.2f", Double.parseDouble(expectedAmount));
        }

        String regexAmount = expectedAmount.replace("$", "\\$");
        String validationRegex = "^" + regexAmount + " has been transferred from account #(\\d+) to account #(\\d+)\\.$";

        org.testng.Assert.assertTrue(
                actualMessage.matches(validationRegex),
                String.format("Format Mismatch!\nActual: '%s'\nPattern: '%s'", actualMessage, validationRegex)
        );
    }

    @And("the backend database ledger state must reflect a transaction status of {string}")
    public void the_backend_database_ledger_state_must_reflect_a_transaction_status_of(String expectedDbStatus) {
        boolean isDbValidationActive = Boolean.parseBoolean(ConfigReader.get("db.validation.enabled"));
        if (!isDbValidationActive) {
            LOG.warn("Database Audit Warning: 'db.validation.enabled' is false. Skipping transaction verification step.");
            return;
        }

        String sqlQuery = "SELECT transaction_status FROM bank_ledger WHERE from_account = ? AND amount = ? ORDER BY timestamp DESC LIMIT 1";
        String expectedAmount = context.getStringContext("TX_AMOUNT");
        String expectedFrom = context.getStringContext("TX_FROM");

        String sanitizedAmountStr = expectedAmount.replaceAll("[\\$, ]", "").trim();
        if (sanitizedAmountStr.isEmpty()) {
            org.testng.Assert.fail("AUTOMATION ERROR: Context execution transaction evaluate string parameter returned blank value.");
        }

        java.math.BigDecimal amountForQuery = new java.math.BigDecimal(sanitizedAmountStr).setScale(2, java.math.RoundingMode.HALF_UP);

        // This call will now safely fail fast with structural details if zero rows are returned
        String actualDbStatus = DatabaseUtil.getSingleValue(sqlQuery, "transaction_status", expectedFrom, amountForQuery);

        org.testng.Assert.assertEquals(actualDbStatus, expectedDbStatus,
                String.format("CRITICAL DESYNC FAILURE: UI displayed success, but DB ledger transaction status resolved to: '%s'", actualDbStatus));
    }
}