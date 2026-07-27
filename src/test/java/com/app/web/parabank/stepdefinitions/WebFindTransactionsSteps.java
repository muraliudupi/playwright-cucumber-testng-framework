package com.app.web.parabank.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.app.web.parabank.pages.WebFindTransactionsPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

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
        webFindTransactionsPage.navigateToFindTransactions().searchByToday();
    }

    @Then("the bill payment transaction appears in the results")
    public void the_bill_payment_transaction_appears_in_the_results() {
        String payeeName = context.getStringContext("BILLPAY_PAYEE_NAME");
        assertTrue(webFindTransactionsPage.resultsContain(payeeName),
                String.format("Find Transactions Failure: no result row referencing payee '%s' found for today's date.", payeeName));
    }
}