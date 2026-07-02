package com.framework.pages;

import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;

/**
 * Page Object for ParaBank's "Customer Login" panel (present on the site's index page). Selectors use stable id attributes rather than text/CSS
 * that's likely to shift with minor markup tweaks.
 */
public class LoginPage extends BasePage {

    private Locator usernameInput() {
        return page().locator("input[name='username']");
    }

    private Locator passwordInput() {
        return page().locator("input[name='password']");
    }

    private Locator loginButton() {
        return page().locator("input[value='Log In']");
    }

    private Locator welcomeMessage() {
        return page().locator("#leftPanel")
                .getByText("Welcome", new Locator.GetByTextOptions().setExact(false));
    }

    private Locator accountsOverviewHeading() {
        return page().locator("#rightPanel h1.title:has-text('Accounts Overview')");
    }

    private Locator errorMessage() {
        return page().locator("#rightPanel h1.title:has-text('Error!')");
    }

    public LoginPage open() {
        page().navigate(ConfigReader.get("baseUrl"));
        return this;
    }

    public LoginPage login(String username, String password) {
        usernameInput().fill(username);
        passwordInput().fill(password);
        loginButton().click();
        return this;
    }

    public boolean isLoginSuccessful() {
        return accountsOverviewHeading().isVisible();
    }

    public void verifyLoginSuccessful() {
        accountsOverviewHeading().waitFor(new Locator.WaitForOptions().setTimeout(10_000));
        welcomeMessage().waitFor(new Locator.WaitForOptions().setTimeout(10_000));
    }

    public String getErrorMessage() {
        return errorMessage().isVisible() ? errorMessage().innerText() : null;
    }
}
