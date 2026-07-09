package com.app.mobile.saucelabs.stepdefinitions;

import com.app.web.parabank.stepdefinitions.WebBaseSteps;
import com.framework.context.ScenarioContext;
import com.app.mobile.saucelabs.pages.MobileLoginPage;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.Map;

public class MobileLoginSteps extends WebBaseSteps {

    private final MobileLoginPage loginPage;
    private final ScenarioContext context;

    public MobileLoginSteps(MobileLoginPage loginPage, ScenarioContext context) {
        this.loginPage = loginPage;
        this.context = context;

    }

    @Given("the user is on the mobile login screen")
    public void the_user_is_on_the_mobile_login_screen() {
        loginPage.open();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        loginPage.login(username, password);
    }

    @When("the user logs into the mobile app using credentials from data key {string} sheet {string}")
    public void the_user_logs_into_mobile_app_using_credentials_from_data_key(String testCaseId, String sheetName){
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        context.setContext("USER_DATA", rowData);

        String username = rowData.get("Username");
        String password = rowData.get("Password");

        loginPage.login(username, password);
    }

    @Then("the mobile dashboard should be displayed")
    public void the_mobile_dashboard_should_be_displayed() {

        boolean dashboardCheckResult = loginPage.verifyDashboard();
        Assert.assertTrue(dashboardCheckResult,
                "Cross-Platform Validation Failure: Product Catalog header dashboard missing.");

        boolean loginCheckResult = loginPage.isLogoutOptionDisplayed();
        Assert.assertTrue(loginCheckResult,
                "Authentication State Failure: 'Log Out' navigation menu item was not visible after login submission.");
    }
}