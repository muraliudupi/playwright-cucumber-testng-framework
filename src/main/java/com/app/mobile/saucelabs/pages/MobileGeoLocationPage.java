package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

public class MobileGeoLocationPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/locationTV")
    private WebElement lblTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/latitudeTV")
    private WebElement lblLatitude;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/longitudeTV")
    private WebElement lblLongitude;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/startBtn")
    private WebElement btnStartObserving;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/stopBtn")
    private WebElement btnStopObserving;

    private static final By ALLOW_WHILE_USING_APP =
            By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");

    public void grantLocationPermissionIfPrompted() {
        try {
            wait(longWait())
                    .until(ExpectedConditions.elementToBeClickable(ALLOW_WHILE_USING_APP))
                    .click();
            LOG.info("Dialog appeared — permission granted.");
        } catch (Exception e) {
            LOG.info("Dialog didn't appear — permission already granted from a prior run.");
        }
    }

    public boolean isGeoLocationScreenDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblTitle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLatitude() {
        ensureElementsInitialized();
        return wait(longWait()).until(ExpectedConditions.visibilityOf(lblLatitude)).getText().trim();
    }

    public String getLongitude() {
        ensureElementsInitialized();
        return wait(longWait()).until(ExpectedConditions.visibilityOf(lblLongitude)).getText().trim();
    }

    public boolean coordinatesArePopulated(int timeoutSeconds) {
        ensureElementsInitialized();
        try {
            wait(Duration.ofSeconds(timeoutSeconds)).until(d -> !getLatitude().isBlank() && !getLongitude().isBlank());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void stopObserving() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnStopObserving)).click();
    }

    public void startObserving() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnStartObserving)).click();
    }
}