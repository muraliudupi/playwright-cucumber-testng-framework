package com.app.mobile.saucelabs.pages;

import com.framework.core.MobileDriverFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class MobileBasePage {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    private boolean isInitialized = false;

    protected MobileBasePage() {
    }

    protected synchronized void ensureElementsInitialized() {
        if (!isInitialized) {
            AppiumDriver driver = MobileDriverFactory.getDriver();
            long threadId = Thread.currentThread().threadId();

            LOG.debug("[Thread-{}] Lazily binding Appium proxies for page element factory: {}",
                    threadId, this.getClass().getSimpleName());

            PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(5)), this);
            isInitialized = true;
        }
    }

    protected AppiumDriver driver() {
        return MobileDriverFactory.getDriver();
    }

    protected WebDriverWait wait(Duration timeout) {
        return new WebDriverWait(driver(), timeout);
    }
}