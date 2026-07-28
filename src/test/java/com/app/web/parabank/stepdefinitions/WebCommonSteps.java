package com.app.web.parabank.stepdefinitions;

import com.framework.core.WebDriverFactory;
import com.framework.steps.BaseSteps;
import com.microsoft.playwright.Page;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.testng.Assert.assertTrue;

public class WebCommonSteps extends BaseSteps {

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

    private boolean isTextVisible(String text) {
        return WebDriverFactory.getPage()
                .getByText(text, new Page.GetByTextOptions().setExact(true))
                .count() > 0;
    }
}