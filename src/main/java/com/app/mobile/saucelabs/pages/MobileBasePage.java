package com.app.mobile.saucelabs.pages;

import com.framework.core.MobileDriverFactory;
import com.framework.utils.ConfigReader;
import com.framework.utils.MobileScrollUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

            PageFactory.initElements(new AppiumFieldDecorator(driver,
                    Duration.ofSeconds(ConfigReader.getInt("mobile.field.decorator.timeout.sec", 5))), this);

            isInitialized = true;
        }
    }

    protected AppiumDriver driver() {
        return MobileDriverFactory.getDriver();
    }

    protected WebDriverWait wait(Duration timeout) {
        return new WebDriverWait(driver(), timeout);
    }

    protected Duration longWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.wait.timeout.sec", 15));
    }

    protected Duration shortWait() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.element.short.wait.timeout.sec", 10));
    }

    protected Duration existenceCheckTimeout() {
        return Duration.ofSeconds(ConfigReader.getInt("mobile.existence.check.timeout.sec", 3));
    }

    public boolean hasValidationText(String expectedText) {
        AppiumDriver driver = (AppiumDriver) MobileDriverFactory.getDriver();
        int maxScrolls = 3;
        By xpath = By.xpath(String.format("//android.widget.TextView[@text='%s']", expectedText));

        for (int i = 0; i <= maxScrolls; i++) {
            try {
                if (!driver.findElements(xpath).isEmpty() && driver.findElement(xpath).isDisplayed()) {
                    return true;
                }
            } catch (Exception ignored) {}

            if (i < maxScrolls) {
                MobileScrollUtils.scrollDown(driver);
            }
        }
        return false;
    }

    public boolean isElementVisible(WebElement element) {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}