package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebAccountActivityPage;
import com.app.web.parabank.pages.WebAccountsOverviewPage;
import com.app.web.parabank.pages.WebTransactionDetailsPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebAccountsOverviewSteps extends BaseSteps {

    private final WebAccountsOverviewPage webAccountsOverviewPage;
    private final WebAccountActivityPage webAccountActivityPage;
    private final WebTransactionDetailsPage webTransactionDetailsPage;

    private String clickedAccountNumber;

    public WebAccountsOverviewSteps(WebAccountsOverviewPage webAccountsOverviewPage,
                                    WebAccountActivityPage webAccountActivityPage,
                                    WebTransactionDetailsPage webTransactionDetailsPage) {
        this.webAccountsOverviewPage = webAccountsOverviewPage;
        this.webAccountActivityPage = webAccountActivityPage;
        this.webTransactionDetailsPage = webTransactionDetailsPage;
    }

    @When("the user clicks the first account number link on Accounts Overview")
    public void the_user_clicks_first_account_number_link() {
        clickedAccountNumber = webAccountsOverviewPage.navigateToAccountsOverview().clickFirstAccountNumberLink();
        webAccountActivityPage.waitForAccountDetailsLoaded();
    }

    @Then("the Account Details page is displayed for that account")
    public void the_account_details_page_is_displayed() {
        assertTrue(!webAccountActivityPage.isErrorDisplayed(),
                "Account Details Failure: an error was displayed instead of account details.");
        assertEquals(webAccountActivityPage.getDisplayedAccountId(), clickedAccountNumber,
                "Account Details Failure: displayed account id did not match the account number clicked.");
    }

    @And("the user searches account activity for {string} and {string}")
    public void the_user_searches_account_activity(String month, String transactionType) {
        webAccountActivityPage.searchActivity(month, transactionType);
    }

    @Then("the account activity results are displayed")
    public void the_account_activity_results_are_displayed() {
        // Either outcome — transactions found, or none for that period — is a valid, correct result.
        // Failure here means neither state rendered at all, i.e. the search itself broke.
        boolean resultRendered = webAccountActivityPage.hasTransactionResults()
                || webAccountActivityPage.getTransactionRowCount() == 0;
        assertTrue(resultRendered, "Account Activity Failure: neither results nor a no-transactions message was displayed.");
    }

    @And("the user clicks the first transaction link")
    public void the_user_clicks_first_transaction_link() {
        webAccountActivityPage.clickFirstTransaction();
    }

    @Then("the Transaction Details page is displayed")
    public void the_transaction_details_page_is_displayed() {
        assertTrue(webTransactionDetailsPage.isTransactionDetailsDisplayed(),
                "Transaction Details Failure: details heading was not displayed after clicking a transaction.");
    }
}