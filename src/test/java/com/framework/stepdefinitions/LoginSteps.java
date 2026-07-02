package com.framework.stepdefinitions;

import com.framework.pages.LoginPage;
import com.framework.utils.ExcelReader;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.assertTrue;

public class LoginSteps {

    private final LoginPage loginPage;
    private final String excelPath = "src/test/resources/testdata/ParaBankTestData.xlsx";

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
        // Convert Scenario Outline string row parameter to zero-indexed data pointer
        int rowIndex = Integer.parseInt(rowNumber) - 1;

        List<Map<String, String>> testData = ExcelReader.getSheetData(excelPath, sheetName);
        Map<String, String> rowData = testData.get(rowIndex);

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