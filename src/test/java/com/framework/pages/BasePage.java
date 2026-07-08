package com.framework.pages;

import com.framework.core.DriverFactory;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected Page page() {
        return DriverFactory.getPage();
    }
}
