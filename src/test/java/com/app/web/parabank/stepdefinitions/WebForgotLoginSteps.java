package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebForgotLoginPage;
import com.framework.context.ContextKeys;
import com.framework.context.ScenarioContext;
import com.framework.steps.BaseSteps;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import static org.testng.Assert.assertTrue;

public class WebForgotLoginSteps extends BaseSteps {

    private final WebForgotLoginPage webForgotLoginPage;
    private final ScenarioContext context;

    public WebForgotLoginSteps(WebForgotLoginPage webForgotLoginPage, ScenarioContext context) {
        this.webForgotLoginPage = webForgotLoginPage;
        this.context = context;
    }

    @And("the user looks up login info using the registered user's details")
    public void the_user_looks_up_login_info_using_registered_details() {
        webForgotLoginPage.navigateToForgotLogin().submitLookup(
                context.getStringContext(ContextKeys.REGISTERED_FIRST_NAME),
                context.getStringContext(ContextKeys.REGISTERED_LAST_NAME),
                context.getStringContext(ContextKeys.REGISTERED_ADDRESS),
                context.getStringContext(ContextKeys.REGISTERED_CITY),
                context.getStringContext(ContextKeys.REGISTERED_STATE),
                context.getStringContext(ContextKeys.REGISTERED_ZIP),
                context.getStringContext(ContextKeys.REGISTERED_SSN));
    }

    @Then("the login info lookup is confirmed and shows the registered credentials")
    public void the_login_info_lookup_is_confirmed() {
        assertTrue(webForgotLoginPage.isLookupConfirmed(), "Lookup Failure: confirmation text was not displayed.");
        assertTrue(webForgotLoginPage.isDisplayedCredential(context.getStringContext(ContextKeys.REGISTERED_USERNAME)),
                "Lookup Failure: registered username was not shown on the confirmation page.");
    }

    @And("the user submits the login info lookup without entering any values")
    public void the_user_submits_lookup_without_values() {
        webForgotLoginPage.navigateToForgotLogin().submitEmptyLookup();
    }

    @And("the user looks up login info with details that do not match any customer")
    public void the_user_looks_up_login_info_with_unmatched_details() {
        webForgotLoginPage.navigateToForgotLogin().submitLookup(
                "Nonexistent", "Person", "000 Nowhere Ave", "Ghosttown", "ZZ", "00000", "000000000");
    }
}