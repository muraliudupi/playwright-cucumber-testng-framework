package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebLoginPage;
import com.framework.steps.BaseSteps;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebCommonSteps extends BaseSteps {

    private final WebLoginPage webLoginPage;

    public WebCommonSteps(WebLoginPage webLoginPage) {
        this.webLoginPage = webLoginPage;
    }

    @Then("the error message {string} is displayed")
    public void the_error_message_is_displayed(String expectedText) {
        assertTrue(webLoginPage.isTextVisible(expectedText),
                String.format("Expected error message '%s' was not found on the page.", expectedText));
    }

    @Then("the following error messages are displayed:")
    public void the_following_error_messages_are_displayed(DataTable dataTable) {
        List<String> expectedMessages = dataTable.asList();
        for (String expected : expectedMessages) {
            assertTrue(webLoginPage.isTextVisible(expected),
                    String.format("Expected error message '%s' was not found on the page.", expected));
        }
    }

    @When("the user navigates directly to {string} without an active session")
    public void the_user_navigates_directly_to_without_an_active_session(String relativePath) {
        webLoginPage.navigateTo(relativePath);
    }

    @Then("the user is redirected to the login page with {string} error")
    public void the_user_is_redirected_to_the_login_page_with_error(String expectedMessage) {
        assertTrue(webLoginPage.isLoggedOut(),
                "Expected the login form to be shown after unauthenticated access to a protected page, but it was not visible.");

        String actualMessage = webLoginPage.getErrorMessage();
        assertEquals(actualMessage, expectedMessage,
                String.format("Error Message Mismatch: expected '%s' but got '%s'.", expectedMessage, actualMessage));
    }
}