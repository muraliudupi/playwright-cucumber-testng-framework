package com.framework.stepdefinitions;

import com.framework.pages.OpenAccountPage;
import com.framework.utils.ConfigReader;
import com.framework.utils.ExcelReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAccountSteps {

    private final OpenAccountPage openAccountPage;
    private static final Logger LOG = LoggerFactory.getLogger(OpenAccountSteps.class);
    private final String excelFilePath = ConfigReader.getExcelPath();

    private String capturedNewAccountId;
    private String expectedFundingAccount;

    public OpenAccountSteps(OpenAccountPage openAccountPage) {
        this.openAccountPage = openAccountPage;
    }

    @And("the user navigates to the Open New Account module")
    public void the_user_navigates_to_the_open_new_account_module() {
        openAccountPage.navigateToOpenAccount();
    }

    @And("requests a new {string} account using funding account from excel row {string} sheet {string}")
    public void requests_a_new_account_using_funding_account(String accountType, String rowNumber, String sheetName) {
        int rowIndex = Integer.parseInt(rowNumber) - 1;
        List<Map<String, String>> testData = ExcelReader.getSheetData(excelFilePath, sheetName);
        Map<String, String> rowData = testData.get(rowIndex);

        expectedFundingAccount = rowData.get("FromAccount");

        openAccountPage.configureAndOpenAccount(accountType, expectedFundingAccount);
    }

    @Then("the system creates the account showing a confirmation page")
    public void the_system_creates_the_account_showing_a_confirmation_page() {
        openAccountPage.verifyAccountCreationLayoutVisible();
        capturedNewAccountId = openAccountPage.getGeneratedAccountId();

        org.testng.Assert.assertFalse(capturedNewAccountId.isEmpty(), "Generated Account ID was blank!");
        LOG.info("UI Confirmation checked out. New dynamic account generated: [{}]", capturedNewAccountId);
    }

    @And("the backend account ledger table must confirm the new account type is {string}")
    public void the_backend_account_ledger_table_must_confirm_the_new_account_type(String expectedType) {
        String query = "SELECT account_type FROM customer_accounts WHERE account_id = ?";

        String actualDbAccountType = DatabaseUtil.getSingleValue(query, "account_type", capturedNewAccountId);

        org.testng.Assert.assertEquals(
                actualDbAccountType,
                expectedType.toUpperCase(),
                String.format("DATABASE AUDIT FAILURE: New Account ID %s is out of sync or missing in the DB ledger!", capturedNewAccountId)
        );

        LOG.info("Enterprise Verification Success: Database reflects account type {} for ID {}", actualDbAccountType, capturedNewAccountId);
    }
}