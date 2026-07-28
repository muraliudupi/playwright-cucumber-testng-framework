package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebRequestLoanPage;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.Map;

import static org.testng.Assert.assertTrue;

public class WebRequestLoanSteps extends BaseSteps {

    private final WebRequestLoanPage webRequestLoanPage;
    private final ScenarioContext context;

    public WebRequestLoanSteps(WebRequestLoanPage webRequestLoanPage, ScenarioContext context) {
        this.webRequestLoanPage = webRequestLoanPage;
        this.context = context;
    }

    @And("the user requests a loan using data key {string} sheet {string}")
    public void the_user_requests_a_loan_using_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        context.setContext("LOAN_EXPECTED_STATUS", rowData.get("ExpectedStatus"));

        webRequestLoanPage.navigateToRequestLoan().requestLoan(
                rowData.get("LoanAmount"), rowData.get("DownPayment"), rowData.get("FromAccount"));
    }

    @Then("the loan status matches the expected outcome")
    public void the_loan_status_matches_the_expected_outcome() {
        String expected = context.getStringContext("LOAN_EXPECTED_STATUS");
        boolean actualMatches = "Approved".equals(expected)
                ? webRequestLoanPage.isLoanApproved()
                : webRequestLoanPage.isLoanDenied();

        assertTrue(actualMatches,
                String.format("Loan Request Failure: expected status '%s' was not confirmed on the result screen.", expected));
    }

    @And("the user navigates to Request Loan and applies without entering any values")
    public void the_user_applies_for_loan_without_values() {
        webRequestLoanPage.navigateToRequestLoan().applyForLoanWithoutValues();
    }

    @And("the user navigates to Request Loan and applies by entering invalid values")
    public void the_user_applies_for_loan_with_invalid_values() {
        webRequestLoanPage.navigateToRequestLoan().applyForLoanWithInvalidValues();
    }
}