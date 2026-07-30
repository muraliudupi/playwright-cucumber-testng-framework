package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebRegisterPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.Map;
import static org.testng.Assert.assertTrue;

public class WebRegisterSteps extends BaseSteps {

    private final WebRegisterPage webRegisterPage;
    private final ScenarioContext context;

    public WebRegisterSteps(WebRegisterPage webRegisterPage, ScenarioContext context) {
        this.webRegisterPage = webRegisterPage;
        this.context = context;
    }

    @And("the user registers a new user using data key {string} sheet {string}")
    public void the_user_registers_new_user_using_data_key(String testCaseId, String sheetName) {
        Map<String, String> rowData = getExcelRowByKey(testCaseId, sheetName);

        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String uniqueUsername = rowData.get("Username") + uniqueSuffix;
        String uniqueSsn = uniqueSuffix;

        webRegisterPage.navigateToRegister().submitNewRegistration(
                rowData.get("FirstName"), rowData.get("LastName"), rowData.get("Address"), rowData.get("City"),
                rowData.get("State"), rowData.get("ZipCode"), rowData.get("Phone"), uniqueSsn,
                uniqueUsername, rowData.get("Password"));

        context.setContext(ContextKeys.REGISTERED_USERNAME, uniqueUsername);
        context.setContext(ContextKeys.REGISTERED_PASSWORD, rowData.get("Password"));
        context.setContext(ContextKeys.REGISTERED_FIRST_NAME, rowData.get("FirstName"));
        context.setContext(ContextKeys.REGISTERED_LAST_NAME, rowData.get("LastName"));
        context.setContext(ContextKeys.REGISTERED_ADDRESS, rowData.get("Address"));
        context.setContext(ContextKeys.REGISTERED_CITY, rowData.get("City"));
        context.setContext(ContextKeys.REGISTERED_STATE, rowData.get("State"));
        context.setContext(ContextKeys.REGISTERED_ZIP, rowData.get("ZipCode"));
        context.setContext(ContextKeys.REGISTERED_SSN, uniqueSsn);
    }

    @Then("the registration is confirmed")
    public void the_registration_is_confirmed() {
        assertTrue(webRegisterPage.isRegistrationConfirmed(),
                "Registration Failure: success confirmation text was not displayed.");
    }

    @And("the user attempts to register with an existing username {string}")
    public void the_user_attempts_to_register_with_existing_username(String existingUsername) {
        webRegisterPage.navigateToRegister().submitNewRegistration(
                "Test", "User", "123 Test St", "Testville", "CA", "90000", "1234567890", "123456789",
                existingUsername, "TestPass123");
    }

    @And("the user submits the registration form without entering any values")
    public void the_user_submits_registration_without_values() {
        webRegisterPage.navigateToRegister().submitEmptyRegistrationForm();
    }

    @And("the user registers with mismatched passwords {string} and {string}")
    public void the_user_registers_with_mismatched_passwords(String password1, String password2) {
        webRegisterPage.navigateToRegister();
        webRegisterPage.submitMismatchedPasswords(password1, password2);
    }
}