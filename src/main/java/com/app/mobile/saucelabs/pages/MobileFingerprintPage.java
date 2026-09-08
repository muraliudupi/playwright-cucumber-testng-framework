package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileFingerprintPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/alertTitle")
    private WebElement lblAlertTitle;

    @AndroidFindBy(id = "android:id/message")
    private WebElement lblAlertMessage;

    @AndroidFindBy(id = "android:id/button1")
    private WebElement btnAlertOk;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricTV")
    private WebElement lblFingerprintTitle;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricInfoTV")
    private WebElement lblFingerprintInfo;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricDemoInfoTV")
    private WebElement lblFingerprintDemoInfo;

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/bioMetricSw")
    private WebElement swBiometricToggle;

    public boolean isBiometricAlertDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getBiometricAlertMessage() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).getText();
    }

    public void dismissBiometricAlert() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnAlertOk)).click();
    }

    public boolean isFingerprintScreenDisplayed() {
        ensureElementsInitialized();
        if(isBiometricAlertDisplayed()){
            dismissBiometricAlert();
        }
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintInfo)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblFingerprintDemoInfo)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isBiometricToggleDisabled() {
        ensureElementsInitialized();
        return !wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(swBiometricToggle)).isEnabled();
    }
}