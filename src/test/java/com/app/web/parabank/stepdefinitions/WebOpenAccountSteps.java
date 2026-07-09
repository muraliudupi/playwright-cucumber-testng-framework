package com.app.web.parabank.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.app.web.parabank.pages.WebOpenAccountPage;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import com.framework.utils.DatabaseUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.Map;

public class WebOpenAccountSteps extends BaseSteps {

    private final WebOpenAccountPage webOpenAccountPage;
    private final ScenarioContext context;

    public WebOpenAccountSteps(WebOpenAccountPage webOpenAccountPage, ScenarioContext context) {
        this.webOpenAccountPage = webOpenAccountPage;
        this.context = context;
    }

    @And("the user navigates to the Open New Account module")
    public void the_user_navigates_to_the_open_new_account_module() {
        webOpenAccountPage.navigateToOpenAccount();
    }

    @And("requests a new {string} account using funding account from data key {string} sheet {string}")
    public void requests_a_new_account_using_funding_account(String accountType, String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        String fundingAccount = rowData.get("FromAccount");

        webOpenAccountPage.configureAndOpenAccount(accountType, fundingAccount);
        // Gets first From & Open Account.
        // webOpenAccountPage.configureAndOpenAccount(accountType);
    }

    @Then("the system creates the account showing a confirmation page")
    public void the_system_creates_the_account_showing_a_confirmation_page() {
        webOpenAccountPage.verifyAccountCreationLayoutVisible();
        String generatedId = webOpenAccountPage.getGeneratedAccountId();

        org.testng.Assert.assertFalse(generatedId.isEmpty(), "Generated Account ID was blank!");

        context.setContext("SHARED_ACCOUNT_ID", generatedId);
        LOG.info("UI Confirmation verified. Isolated runtime context mapping bounded for ID: [{}]", generatedId);
    }

    @And("the backend account ledger table must confirm the new account type is {string}")
    public void the_backend_account_ledger_table_must_confirm_the_new_account_type(String expectedType) {
        boolean isDbValidationActive = Boolean.parseBoolean(ConfigReader.get("db.validation.enabled"));
        if (!isDbValidationActive) {
            LOG.warn("Database Audit Warning: 'db.validation.enabled' is false. Skipping account ledger verification step.");
            return;
        }

        String query = "SELECT account_type FROM customer_accounts WHERE account_id = ?";
        String targetAccountId = context.getStringContext("SHARED_ACCOUNT_ID");

        String actualDbAccountType = DatabaseUtil.getSingleValueWithRetry(5, 500, query, "account_type", targetAccountId);

        org.testng.Assert.assertEquals(
                actualDbAccountType,
                expectedType.toUpperCase(),
                String.format("DATABASE AUDIT FAILURE: New Account ID %s is out of sync or missing in DB ledger!", targetAccountId)
        );

        LOG.info("Ecosystem Integration Checked: Database verified type {} for ID {}", actualDbAccountType, targetAccountId);
    }
}