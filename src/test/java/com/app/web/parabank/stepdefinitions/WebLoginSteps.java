package com.app.web.parabank.stepdefinitions;

import com.framework.context.ScenarioContext;
import com.app.web.parabank.pages.WebLoginPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import static org.testng.Assert.assertTrue;

public class WebLoginSteps extends BaseSteps {

    private final WebLoginPage webLoginPage;
    private final ScenarioContext context;

    public WebLoginSteps(WebLoginPage webLoginPage, ScenarioContext context) {
        this.webLoginPage = webLoginPage;
        this.context = context;
    }

    @Given("the user is on the ParaBank login page")
    public void the_user_is_on_the_parabank_login_page() {
        webLoginPage.open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        webLoginPage.login(username, password);
    }

    @When("the user logs in using credentials from data key {string} sheet {string}")
    public void the_user_logs_in_using_credentials_from_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        context.setContext("USER_DATA", rowData);

        String username = rowData.get("Username");
        String password = rowData.get("Password");

        webLoginPage.login(username, password);
    }

    @Then("the Welcome message and the Accounts Overview page are displayed")
    public void the_welcome_message_and_accounts_overview_page_are_displayed() {
        webLoginPage.verifyLoginSuccessful();
        assertTrue(webLoginPage.isLoginSuccessful(), "Accounts Overview heading was not visible after login.");
    }
}
