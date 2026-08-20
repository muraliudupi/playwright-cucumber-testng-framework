package com.app.mobile.saucelabs.pages;

import io.appium.java_client.remote.SupportsContextSwitching;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.net.URI;
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

    public boolean verifyLoadedUrlMatches(String url, int timeoutSeconds) {
        ensureElementsInitialized();
        String expectedHost = extractHost(url);
        SupportsContextSwitching contextDriver = (SupportsContextSwitching) driver();

        try {
            wait(Duration.ofSeconds(timeoutSeconds)).until(d ->
                    contextDriver.getContextHandles().stream().anyMatch(c -> c.startsWith("WEBVIEW")));

            String webviewContext = contextDriver.getContextHandles().stream()
                    .filter(c -> c.startsWith("WEBVIEW"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No WEBVIEW context appeared — this app may not have WebView debugging enabled."));

            contextDriver.context(webviewContext);
            String actualUrl = driver().getCurrentUrl();
            LOG.info("WebView loaded URL: {}", actualUrl);

            return actualUrl != null && expectedHost != null && actualUrl.contains(expectedHost);
        } catch (Exception e) {
            LOG.warn("Could not verify loaded URL via WebView context switch: {}", e.getMessage());
            return false;
        } finally {
            contextDriver.context("NATIVE_APP");
        }
    }

    private String extractHost(String url) {
        try {
            return URI.create(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }
}