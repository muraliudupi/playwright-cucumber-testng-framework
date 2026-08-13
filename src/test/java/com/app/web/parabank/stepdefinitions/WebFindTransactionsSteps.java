package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebFindTransactionsPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.time.LocalDate;
import static org.testng.Assert.assertTrue;

public class WebFindTransactionsSteps extends BaseSteps {

    private final WebFindTransactionsPage webFindTransactionsPage;
    private final ScenarioContext context;

    public WebFindTransactionsSteps(WebFindTransactionsPage webFindTransactionsPage, ScenarioContext context) {
        this.webFindTransactionsPage = webFindTransactionsPage;
        this.context = context;
    }

    @And("the user searches transactions for today")
    public void the_user_searches_transactions_for_today() {
        String account = context.getStringContext(ContextKeys.BILLPAY_FROM_ACCOUNT);
        webFindTransactionsPage.navigateToFindTransactions().searchByToday(account);
    }

    @And("the user searches transactions by date range for today")
    public void the_user_searches_transactions_by_date_range() {
        String account = context.getStringContext(ContextKeys.BILLPAY_FROM_ACCOUNT);
        LocalDate today = LocalDate.now();
        webFindTransactionsPage.navigateToFindTransactions().searchByDateRange(account, today, today);
    }

    @And("the user searches transactions by amount matching the bill payment")
    public void the_user_searches_transactions_by_amount() {
        String account = context.getStringContext(ContextKeys.BILLPAY_FROM_ACCOUNT);
        String amount = context.getStringContext(ContextKeys.BILLPAY_AMOUNT);
        webFindTransactionsPage.navigateToFindTransactions().searchByAmount(account, amount);
    }

    @And("the user searches transactions by the noted transaction ID")
    public void the_user_searches_transactions_by_the_noted_transaction_id() {
        String transactionId = context.getStringContext(ContextKeys.CAPTURED_TRANSACTION_ID);
        webFindTransactionsPage.navigateToFindTransactions().searchByTransactionId(transactionId);
    }

    @Then("the bill payment transaction appears in the results")
    public void the_bill_payment_transaction_appears_in_the_results() {
        String payeeName = context.getStringContext(ContextKeys.BILLPAY_PAYEE_NAME);
        assertTrue(webFindTransactionsPage.hasResults() && webFindTransactionsPage.resultsContain(payeeName),
                String.format("Find Transactions Failure: no result row referencing payee '%s' found for today's date on account '%s'.",
                        payeeName, context.getStringContext(ContextKeys.BILLPAY_FROM_ACCOUNT)));
    }

    @And("the user navigates to Find Transactions")
    public void the_user_navigates_to_find_transactions() {
        webFindTransactionsPage.navigateToFindTransactions();
    }

    @And("the user submits each transaction search with no values entered")
    public void the_user_submits_each_search_with_no_values() {
        webFindTransactionsPage.submitFindByTransactionIdWithoutValue();
        webFindTransactionsPage.submitFindByDateWithoutValue();
        webFindTransactionsPage.submitFindByDateRangeWithoutValues();
        webFindTransactionsPage.submitFindByAmountWithoutValue();
    }

    @And("the user submits each transaction search with invalid values")
    public void the_user_submits_each_search_with_invalid_values() {
        webFindTransactionsPage.submitFindByTransactionIdWithInvalidValue();
        webFindTransactionsPage.submitFindByDateWithInvalidValue();
        webFindTransactionsPage.submitFindByDateRangeWithInvalidValue();
        webFindTransactionsPage.submitFindByAmountWithInvalidValue();
    }
}