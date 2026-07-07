package com.framework.stepdefinitions;

import com.framework.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import static org.testng.Assert.assertTrue;

public class LoginSteps extends BaseSteps {

    private final LoginPage loginPage;

    public LoginSteps(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @Given("the user is on the ParaBank login page")
    public void the_user_is_on_the_parabank_login_page() {
        loginPage.open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        loginPage.login(username, password);
    }

    @When("the user logs in using credentials from excel row {string} sheet {string}")
    public void the_user_logs_in_using_credentials_from_excel_row_sheet(String rowNumber, String sheetName) {
        Map<String, String> rowData = getExcelRow(sheetName, rowNumber);

        String username = rowData.get("Username");
        String password = rowData.get("Password");

        loginPage.login(username, password);
    }

    @Then("the Welcome message and the Accounts Overview page are displayed")
    public void the_welcome_message_and_accounts_overview_page_are_displayed() {
        loginPage.verifyLoginSuccessful();
        assertTrue(loginPage.isLoginSuccessful(), "Accounts Overview heading was not visible after login.");
    }
}
