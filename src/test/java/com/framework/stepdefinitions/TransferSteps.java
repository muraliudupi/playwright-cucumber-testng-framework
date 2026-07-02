package com.framework.stepdefinitions;

import com.framework.pages.LoginPage;
import com.framework.pages.TransferPage;
import com.framework.utils.ExcelReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransferSteps {

    private final LoginPage loginPage;
    private final TransferPage transferPage;
    private final String excelPath = "src/test/resources/testdata/ParaBankTestData.xlsx";
    private static final Logger LOG = LoggerFactory.getLogger(TransferSteps.class);

    // Thread-safe runtime variables scoped exclusively to this specific execution thread
    private String expectedAmount;
    private String expectedFrom;
    private String expectedTo;

    // PicoContainer injects isolated page frameworks dynamically
    public TransferSteps(LoginPage loginPage, TransferPage transferPage) {
        this.loginPage = loginPage;
        this.transferPage = transferPage;
    }

    @And("the user navigates to the Transfer Funds interface")
    public void the_user_navigates_to_the_transfer_funds_interface() {
        transferPage.navigateToTransferFunds();
    }

    @And("executes a transfer using data from excel row {string} sheet {string}")
    public void executes_a_transfer_using_data_from_excel_row_sheet(String rowNumber, String sheetName) {
        int rowIndex = Integer.parseInt(rowNumber) - 1;
        List<Map<String, String>> testData = ExcelReader.getSheetData(excelPath, sheetName);
        Map<String, String> rowData = testData.get(rowIndex);

        // Store values locally for evaluation in the assertion step
        this.expectedAmount = rowData.get("Amount");
        this.expectedFrom = rowData.get("FromAccount");
        this.expectedTo = rowData.get("ToAccount");

        transferPage.executeTransfer(expectedAmount, expectedFrom, expectedTo);
    }

    @Then("the transfer completes successfully with a validated dynamic confirmation message")
    public void the_transfer_completes_successfully_with_a_validated_dynamic_confirmation_message() {
        // Ensure processing layout is visible
        transferPage.verifyTransferLayoutVisible();

        // Standardize your current scenario amount context ($10.00)
        if (!expectedAmount.startsWith("$")) {
            expectedAmount = "$" + String.format("%.2f", Double.parseDouble(expectedAmount));
        }

        // Capture the runtime string printed by the UI
        String actualMessage = transferPage.getActualResultMessage();

        // Escape the dollar sign for regex compilation safety
        String regexAmount = expectedAmount.replace("$", "\\$");

        /* * COMPACT REGEX LAYOUT:
         * ^\\Q...\\E matches the expected clean prefix text literal.
         * (\\d+) captures any dynamic sequence of numeric characters representing the true account IDs.
         */
        String validationRegex = "^" + regexAmount + " has been transferred from account #(\\d+) to account #(\\d+)\\.$";

        // Structural Architecture Verification Match
        org.testng.Assert.assertTrue(
                actualMessage.matches(validationRegex),
                String.format("Format Mismatch! Content did not follow the generic template.\nActual: '%s'\nPattern: '%s'", actualMessage, validationRegex)
        );

        // Optional architectural logging for your test pipeline trails
        LOG.info("Generic matching passed cleanly for confirmation text: [{}]", actualMessage);
    }
}