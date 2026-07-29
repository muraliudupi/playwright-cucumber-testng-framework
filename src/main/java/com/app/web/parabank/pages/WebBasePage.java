package com.app.web.parabank.pages;

import com.framework.core.WebDriverFactory;
import com.framework.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebBasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return WebDriverFactory.getPage();
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
            dropdown.selectOption(new SelectOption().setIndex(fallbackIndex));
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
}
