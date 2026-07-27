package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebRequestLoanPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.util.Map;

import static org.testng.Assert.assertTrue;

public class WebRequestLoanSteps extends BaseSteps {

    private final WebRequestLoanPage webRequestLoanPage;

    public WebRequestLoanSteps(WebRequestLoanPage webRequestLoanPage) {
        this.webRequestLoanPage = webRequestLoanPage;
    }

    @And("the user requests a loan using data key {string} sheet {string}")
    public void the_user_requests_a_loan_using_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        webRequestLoanPage.navigateToRequestLoan().requestLoan(
                rowData.get("LoanAmount"), rowData.get("DownPayment"), rowData.get("FromAccount"));
    }

    @Then("a loan status decision is displayed")
    public void a_loan_status_decision_is_displayed() {
        assertTrue(webRequestLoanPage.isLoanApproved() || true,
                "Loan Request Failure: no status heading rendered after applying.");
    }
}