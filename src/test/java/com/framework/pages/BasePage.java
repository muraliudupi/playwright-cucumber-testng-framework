package com.framework.pages;

import com.framework.core.DriverFactory;
import com.microsoft.playwright.Page;

/**
 * Every Page Object extends this instead of holding its own Page field. Because getPage() resolves DriverFactory's ThreadLocal at CALL TIME
 * (not construction time), Page Objects are safe to instantiate freely — even as static/shared helpers — without ever risking a stale or
 * cross-thread Page reference.
 */
public abstract class BasePage {

    protected Page page() {
        return DriverFactory.getPage();
    }
}
