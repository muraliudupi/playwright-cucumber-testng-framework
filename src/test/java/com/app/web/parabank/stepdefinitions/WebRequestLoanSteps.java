package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebAccountsOverviewPage;
import com.app.web.parabank.pages.WebAdministrationPage;
import com.app.web.parabank.pages.WebRequestLoanPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.models.RequestLoanData;
import com.framework.steps.BaseSteps;
import com.framework.utils.LoanScenarioCalculator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.math.BigDecimal;
import static org.testng.Assert.assertTrue;

public class WebRequestLoanSteps extends BaseSteps {

    private final WebRequestLoanPage webRequestLoanPage;
    private final WebAccountsOverviewPage webAccountsOverviewPage;
    private final WebAdministrationPage webAdministrationPage;
    private final ScenarioContext context;

    public WebRequestLoanSteps(WebRequestLoanPage webRequestLoanPage,
                               WebAccountsOverviewPage webAccountsOverviewPage,
                               WebAdministrationPage webAdministrationPage, ScenarioContext context) {

        this.webRequestLoanPage = webRequestLoanPage;
        this.webAccountsOverviewPage = webAccountsOverviewPage;
        this.webAdministrationPage = webAdministrationPage;
        this.context = context;
    }

    @And("the user requests a loan using data key {string} sheet {string}")
    public void the_user_requests_a_loan_using_data_key(String testCaseId, String sheetName) {
        RequestLoanData requestLoanData = getExcelModelByKey(testCaseId, sheetName, RequestLoanData::fromMap);
        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, requestLoanData.expectedStatus());

        webRequestLoanPage.navigateToRequestLoan().requestLoan(requestLoanData);
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
        BigDecimal totalBalance = webAccountsOverviewPage
                .navigateToAccountsOverview()
                .calculateTotalBalanceAmount();

        WebAdministrationPage.LoanProcess loanProcess = webAdministrationPage
                .navigateToAdministration()
                .findloanProcessorAndthreshold();

        LoanScenarioCalculator.LoanTerms approvalTerms = LoanScenarioCalculator.forApproval(
                loanProcess.loanProcessor(), loanProcess.threshold(), totalBalance);
        LoanScenarioCalculator.LoanTerms denialTerms = LoanScenarioCalculator.forDenial(
                loanProcess.loanProcessor(), loanProcess.threshold(), totalBalance);

        context.setContext(ContextKeys.LOAN_APPROVAL_AMOUNT, approvalTerms.loanAmount());
        context.setContext(ContextKeys.LOAN_APPROVAL_DOWN_PAYMENT, approvalTerms.downPayment());
        context.setContext(ContextKeys.LOAN_DENIAL_AMOUNT, denialTerms.loanAmount());
        context.setContext(ContextKeys.LOAN_DENIAL_DOWN_PAYMENT, denialTerms.downPayment());
    }


    @And("the user requests a loan for less than the noted amount")
    public void the_user_requests_loan_less_than_noted_amount() {
        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, "Approved");
        webRequestLoanPage.navigateToRequestLoan().requestLoan(
                context.getStringContext(ContextKeys.LOAN_APPROVAL_AMOUNT),
                context.getStringContext(ContextKeys.LOAN_APPROVAL_DOWN_PAYMENT));
    }

    @And("the user requests a loan for much more than the noted amount")
    public void the_user_requests_loan_more_than_noted_amount() {
        context.setContext(ContextKeys.LOAN_EXPECTED_STATUS, "Denied");
        webRequestLoanPage.navigateToRequestLoan().requestLoan(
                context.getStringContext(ContextKeys.LOAN_DENIAL_AMOUNT),
                context.getStringContext(ContextKeys.LOAN_DENIAL_DOWN_PAYMENT));
    }
}