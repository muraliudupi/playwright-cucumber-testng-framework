package com.app.web.parabank.pages;

import com.framework.core.WebDriverFactory;
import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebBasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return WebDriverFactory.getPage();
    }

    protected void waitUntilReady(Locator locator) {
        page().waitForLoadState(LoadState.NETWORKIDLE);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(ConfigReader.getInt("web.element.wait.timeout.ms", 5000)));
    }

    protected String selectAccountWithFallback(Locator dropdown, String requestedAccount, int fallbackIndex) {
        boolean requestedAccountFound = true;
        try {
            Locator optionTarget = dropdown.locator(String.format("option[value='%s']", requestedAccount));
            optionTarget.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(ConfigReader.getInt("web.dropdown.wait.timeout.ms", 3000)));
            dropdown.selectOption(requestedAccount);
        } catch (Exception e) {
            requestedAccountFound = false;
            try {
                dropdown.selectOption(new SelectOption().setIndex(fallbackIndex));
            } catch (Exception fallbackException) {
                dropdown.selectOption(new SelectOption().setIndex(0));
            }
        }
        String actualAccount = dropdown.inputValue();
        if (!requestedAccountFound) {
            LOG.warn("Requested account '{}' unavailable; framework substituted '{}'.", requestedAccount, actualAccount);
        }
        return actualAccount;
    }

    private Locator logoutLink() {
        return page().locator("#leftPanel a[href='logout.htm']");
    }

    public void logout() {
        logoutLink().click();
    }

    public boolean isLoggedOut() {
        return page().locator("input[value='Log In']").isVisible();
    }

    public void navigateTo(String relativePath) {
        page().navigate(ConfigReader.get("baseUrl") + "parabank/" + relativePath);
    }

    public boolean isTextVisible(String text) {
        Locator locator = page().getByText(text, new Page.GetByTextOptions().setExact(true));
        try {
            locator.first().waitFor(new Locator.WaitForOptions()
                    .setTimeout(ConfigReader.getInt("web.confirmation.wait.timeout.ms", 20000)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
