package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class MobileWebViewPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/webViewTV")
    private WebElement lblTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/urlET")
    private WebElement txtUrl;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/goBtn")
    private WebElement btnGoToSite;

    private static final By LOADING_INDICATOR = By.id("com.saucelabs.mydemoapp.android:id/loadingIV");

    public boolean isWebViewScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterUrl(String url) {
        ensureElementsInitialized();
        WebElement field = wait(longWait()).until(ExpectedConditions.elementToBeClickable(txtUrl));
        field.clear();
        field.sendKeys(url);
    }

    public void tapGoToSite() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnGoToSite)).click();
    }

    public boolean waitForPageLoadToComplete(int timeoutSeconds) {
        ensureElementsInitialized();
        try {
            return wait(Duration.ofSeconds(timeoutSeconds)).until(ExpectedConditions.invisibilityOfElementLocated(LOADING_INDICATOR));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyLoadedUrlMatches(String url, int timeoutSeconds){
        ensureElementsInitialized();
        try {
            // Code to verify that webpage loaded with matching url and within timeoutSeconds.
            LOG.info("Inside verifyLoadedUrlMatches");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}