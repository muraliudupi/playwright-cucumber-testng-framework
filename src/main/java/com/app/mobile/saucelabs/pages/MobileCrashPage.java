package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileCrashPage extends MobileBasePage {

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Crashes\")")
    private WebElement lblCrashesTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cause_uncaught_exception_button")
    private WebElement btnCauseUncaughtException;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/cause_native_crash_button")
    private WebElement btnCauseNativeCrash;

    public boolean isCrashScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblCrashesTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(btnCauseUncaughtException)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(btnCauseNativeCrash)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}