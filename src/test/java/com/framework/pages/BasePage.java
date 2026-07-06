package com.framework.pages;

import com.framework.core.DriverFactory;
import com.microsoft.playwright.Page;

public abstract class BasePage {

    protected Page page() {
        return DriverFactory.getPage();
    }
}
