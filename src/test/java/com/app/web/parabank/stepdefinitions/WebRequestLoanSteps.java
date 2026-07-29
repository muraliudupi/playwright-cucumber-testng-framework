package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebAccountsOverviewPage;
import com.app.web.parabank.pages.WebRequestLoanPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import static org.testng.Assert.assertTrue;

public class WebRequestLoanSteps extends BaseSteps {

    private final WebRequestLoanPage webRequestLoanPage;
    private final WebAccountsOverviewPage webAccountsOverviewPage;
    private final ScenarioContext context;

    public WebRequestLoanSteps(WebRequestLoanPage webRequestLoanPage,
                               WebAccountsOverviewPage webAccountsOverviewPage, ScenarioContext context) {

        this.webRequestLoanPage = webRequestLoanPage;
        this.webAccountsOverviewPage = webAccountsOverviewPage;
        this.context = context;
    }

    @And("the user requests a loan using data key {string} sheet {string}")
    public void the_user_requests_a_loan_using_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);
        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, rowData.get("ExpectedStatus"));

        webRequestLoanPage.navigateToRequestLoan().requestLoan(
                rowData.get("LoanAmount"), rowData.get("DownPayment"), rowData.get("FromAccount"));
    }

    @Then("the loan status matches the expected outcome")
    public void the_loan_status_matches_the_expected_outcome() {
        String expected = context.getStringContext(ContextKeys.LOAN_EXPECTED_STATUS);
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

    @And("the system decides loan amount required for approval")
    public void the_system_decides_loan_amount_required_for_approval() {
        BigDecimal totalAmount = webAccountsOverviewPage
                .navigateToAccountsOverview()
                .calculateTotalAvailableAmount();

        // Need to navigate to Admin page and get 'Threshold' value to calculate 'Loan amount' that can be approved.
        // E.g. if 20% then multiply totalAmount with 0.2 and assign to "loanAmount"

        BigDecimal loanAmount = totalAmount.multiply(new BigDecimal("0.2"));

        context.setContext(ContextKeys.LOAN_AMOUNT, loanAmount);
    }

    @And("the user requests a loan for less than the noted amount")
    public void the_user_requests_loan_less_than_noted_amount() {
        BigDecimal available = new BigDecimal(context.getStringContext(ContextKeys.LOAN_AMOUNT));
        BigDecimal loanAmount = available.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);

        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, "Approved");
        webRequestLoanPage.navigateToRequestLoan()
                .requestLoan(loanAmount.toPlainString(), "10");
    }

    @And("the user requests a loan for much more than the noted amount")
    public void the_user_requests_loan_more_than_noted_amount() {
        BigDecimal available = new BigDecimal(context.getStringContext(ContextKeys.LOAN_AMOUNT));
        BigDecimal loanAmount = available.multiply(BigDecimal.valueOf(100));

        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, "Denied");
        webRequestLoanPage.navigateToRequestLoan()
                .requestLoan(loanAmount.toPlainString(), "10");
    }
}