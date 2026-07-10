package com.app.mobile.saucelabs.stepdefinitions;

import com.app.mobile.saucelabs.pages.MobileProductPage;
import com.framework.context.ScenarioContext;
import com.app.mobile.saucelabs.pages.MobileLoginPage;
import com.framework.steps.BaseSteps;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.Map;

public class MobileLoginLogoutSteps extends BaseSteps {

    private final MobileLoginPage mobileLoginPage;
    protected MobileProductPage mobileProductPage;
    private final ScenarioContext context;

    public MobileLoginLogoutSteps(MobileProductPage productPage, MobileLoginPage loginPage, ScenarioContext context) {
        this.mobileLoginPage = loginPage;
        this.mobileProductPage = productPage;
        this.context = context;
    }

    @Given("the user is on the mobile login screen")
    public void the_user_is_on_the_mobile_login_screen() {
        mobileProductPage.openLoginScreen();
    }

    @When("the user logs in with username {string} and password {string}")
    public void the_user_logs_in_with_username_and_password(String username, String password) {
        mobileLoginPage.login(username, password);
    }

    @When("the user logs into the mobile app using credentials from data key {string} sheet {string}")
    public void the_user_logs_into_mobile_app_using_credentials_from_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        context.setContext("USER_DATA", rowData);

        String username = rowData.get("Username");
        String password = rowData.get("Password");

        mobileLoginPage.login(username, password);
    }

    @Then("the mobile dashboard should be displayed")
    public void the_mobile_dashboard_should_be_displayed() {

        boolean dashboardCheckResult = mobileProductPage.verifyDashboard();
        Assert.assertTrue(dashboardCheckResult,
                "Cross-Platform Validation Failure: Product Catalog header dashboard missing.");

        boolean loginCheckResult = mobileProductPage.isLogoutOptionDisplayed();
        Assert.assertTrue(loginCheckResult,
                "Authentication State Failure: 'Log Out' navigation menu item was not visible after login submission.");
    }

    @When("the user logs out from the mobile app")
    public void the_user_logs_out_from_the_mobile_app() {

        boolean loginCheckResult = mobileProductPage.isLogoutOptionDisplayed();
        Assert.assertTrue(loginCheckResult,
                "Authentication State Failure: 'Log Out' navigation menu item was not visible after login submission.");

        mobileProductPage.logOut();

    }

    @Then("the login screen should be displayed")
    public void the_login_screen_should_be_displayed() {

        boolean result = mobileLoginPage.isLoginOptionDisplayed();
        Assert.assertTrue(result,
                "Authentication State Failure: 'Log In' option was not visible.");

    }
}