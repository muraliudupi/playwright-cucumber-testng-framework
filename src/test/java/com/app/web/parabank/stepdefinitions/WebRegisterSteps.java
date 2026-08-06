package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebRegisterPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.models.RegisterData;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
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
        RegisterData registerData = getExcelModelByKey(testCaseId, sheetName, RegisterData::fromMap);

        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String uniqueUsername = registerData.loginDetails().username() + uniqueSuffix;
        String uniqueSsn = uniqueSuffix;

        webRegisterPage.navigateToRegister().submitNewRegistration(registerData, uniqueSsn, uniqueUsername);

        context.setContext(ContextKeys.REGISTERED_USERNAME, uniqueUsername);
        context.setContext(ContextKeys.REGISTERED_PASSWORD, registerData.loginDetails().password());
        context.setContext(ContextKeys.REGISTERED_FIRST_NAME, registerData.firstName());
        context.setContext(ContextKeys.REGISTERED_LAST_NAME, registerData.lastName());
        context.setContext(ContextKeys.REGISTERED_ADDRESS, registerData.address().address());
        context.setContext(ContextKeys.REGISTERED_CITY, registerData.address().city());
        context.setContext(ContextKeys.REGISTERED_STATE, registerData.address().state());
        context.setContext(ContextKeys.REGISTERED_ZIP, registerData.address().zip());
        context.setContext(ContextKeys.REGISTERED_SSN, uniqueSsn);
    }

    @Then("the registration is confirmed")
    public void the_registration_is_confirmed() {
        assertTrue(webRegisterPage.isRegistrationConfirmed(),
                "Registration Failure: success confirmation text was not displayed.");
    }

    @And("the user attempts to register with an existing username {string}")
    public void the_user_attempts_to_register_with_existing_username(String existingUsername) {
        webRegisterPage.navigateToRegister();
        webRegisterPage.submitNewRegistration(existingUsername);
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