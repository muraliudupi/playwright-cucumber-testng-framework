package com.framework.stepdefinitions;

import com.framework.pages.TransferPage;
import com.framework.utils.ConfigReader;
import com.framework.utils.ExcelReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransferSteps {

    private final TransferPage transferPage;
    String excelFilePath = ConfigReader.getExcelPath();
    private static final Logger LOG = LoggerFactory.getLogger(TransferSteps.class);

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
        int rowIndex = Integer.parseInt(rowNumber) - 1;
        List<Map<String, String>> testData = ExcelReader.getSheetData(excelFilePath, sheetName);
        Map<String, String> rowData = testData.get(rowIndex);

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

        String numericAmount = expectedAmount.replace("$", "").trim();
        String actualDbStatus = DatabaseUtil.getSingleValue(sqlQuery, "transaction_status", expectedFrom, new BigDecimal(numericAmount).setScale(2, RoundingMode.HALF_UP));

        org.testng.Assert.assertEquals(actualDbStatus, expectedDbStatus,
                String.format("CRITICAL LEDGER DESYNC: UI reported success, but Database ledger state was found to be: '%s'", actualDbStatus));

        LOG.info("Thread-Safe Database cross-check complete. Verified transaction state as {}", actualDbStatus);
    }

}