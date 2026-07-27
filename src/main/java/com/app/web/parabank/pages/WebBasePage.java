package com.app.web.parabank.pages;

import com.framework.core.WebDriverFactory;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microsoft.playwright.Locator;

public abstract class WebBasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return WebDriverFactory.getPage();
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
