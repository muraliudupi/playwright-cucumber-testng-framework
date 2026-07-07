package com.framework.stepdefinitions;

import com.framework.pages.TransferPage;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransferSteps extends BaseSteps {

    private final TransferPage transferPage;

    private String expectedAmount;
    private String expectedFrom;
    private String expectedTo;

    public TransferSteps(TransferPage transferPage) {
        this.transferPage = transferPage;
    }

    @And("the user navigates to the Transfer Funds interface")
    public void the_user_navigates_to_the_transfer_funds_interface() {
        transferPage.navigateToTransferFunds();
    }

    @And("executes a transfer using data from excel row {string} sheet {string}")
    public void executes_a_transfer_using_data_from_excel_row_sheet(String rowNumber, String sheetName) {
        Map<String, String> rowData = getExcelRow(sheetName, rowNumber);

        this.expectedAmount = rowData.get("Amount");
        this.expectedFrom = rowData.get("FromAccount");
        this.expectedTo = rowData.get("ToAccount");

        transferPage.executeTransfer(expectedAmount, expectedFrom, expectedTo);
    }

    @Then("the transfer completes successfully with a validated dynamic confirmation message")
    public void the_transfer_completes_successfully_with_a_validated_dynamic_confirmation_message() {
        transferPage.verifyTransferLayoutVisible();
        String actualMessage = transferPage.getActualResultMessage();

        if (!expectedAmount.startsWith("$")) {
            expectedAmount = "$" + String.format("%.2f", Double.parseDouble(expectedAmount));
        }

        String regexAmount = expectedAmount.replace("$", "\\$");
        String validationRegex = "^" + regexAmount + " has been transferred from account #(\\d+) to account #(\\d+)\\.$";

        org.testng.Assert.assertTrue(
                actualMessage.matches(validationRegex),
                String.format("Format Mismatch! Content did not follow the generic template.\nActual: '%s'\nPattern: '%s'", actualMessage, validationRegex)
        );

        LOG.info("Generic matching passed cleanly for confirmation text: [{}]", actualMessage);
    }

    @And("the backend database ledger state must reflect a transaction status of {string}")
    public void the_backend_database_ledger_state_must_reflect_a_transaction_status_of(String expectedDbStatus) {
        String sqlQuery = "SELECT transaction_status FROM bank_ledger WHERE from_account = ? AND amount = ? ORDER BY timestamp DESC LIMIT 1";

        String sanitizedAmountStr = expectedAmount.replaceAll("[\\$, ]", "").trim();

        if (sanitizedAmountStr.isEmpty()) {
            org.testng.Assert.fail("AUTOMATION ARCHITECTURE ERROR: Extracted transaction amount evaluation value is empty or non-numeric.");
        }

        BigDecimal amountForQuery = new BigDecimal(sanitizedAmountStr).setScale(2, RoundingMode.HALF_UP);

        String actualDbStatus = DatabaseUtil.getSingleValue(sqlQuery, "transaction_status", expectedFrom, amountForQuery);

        org.testng.Assert.assertEquals(actualDbStatus, expectedDbStatus,
                String.format("CRITICAL LEDGER DESYNC: UI reported success, but Database ledger state was found to be: '%s'", actualDbStatus));

        LOG.info("Thread-Safe Database cross-check complete. Verified transaction status as {} for parsed numeric context amount [{}]",
                actualDbStatus, amountForQuery);
    }
}
