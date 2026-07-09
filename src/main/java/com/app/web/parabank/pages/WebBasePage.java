package com.app.web.parabank.pages;

import com.framework.core.WebDriverFactory;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebBasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return WebDriverFactory.getPage();
    }
}
