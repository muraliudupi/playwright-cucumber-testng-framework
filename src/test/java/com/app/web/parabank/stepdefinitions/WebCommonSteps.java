package com.app.web.parabank.stepdefinitions;

import com.app.web.parabank.pages.WebLoginPage;
import com.framework.core.WebDriverFactory;
import com.framework.steps.BaseSteps;
import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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
        assertTrue(isTextVisible(expectedText),
                String.format("Expected error message '%s' was not found on the page.", expectedText));
    }

    @Then("the following error messages are displayed:")
    public void the_following_error_messages_are_displayed(DataTable dataTable) {
        List<String> expectedMessages = dataTable.asList();
        for (String expected : expectedMessages) {
            assertTrue(isTextVisible(expected),
                    String.format("Expected error message '%s' was not found on the page.", expected));
        }
    }

    @When("the user navigates directly to {string} without an active session")
    public void the_user_navigates_directly_to_without_an_active_session(String relativePath) {
        WebDriverFactory.getPage()
                .navigate(ConfigReader.get("baseUrl") + "parabank/" + relativePath);
    }

    @Then("the user is redirected to the login page with {string} error")
    public void the_user_is_redirected_to_the_login_page_with_error(String expectedMessage) {
        Locator loginButton = WebDriverFactory.getPage().locator("input[value='Log In']");
        assertTrue(loginButton.isVisible(),
                "Expected the login form to be shown after unauthenticated access to a protected page, but it was not visible.");

        String actualMessage = webLoginPage.getErrorMessage();
        assertEquals(actualMessage, expectedMessage,
                String.format("Error Message Mismatch: expected '%s' but got '%s'.", expectedMessage, actualMessage));
    }

    private boolean isTextVisible(String text) {
        Locator locator = WebDriverFactory.getPage()
                .getByText(text, new Page.GetByTextOptions().setExact(true));
        try {
            locator.first().waitFor(new Locator.WaitForOptions()
                    .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}