package com.app.mobile.saucelabs.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class MobileResetAppPage extends MobileBasePage {

    @AndroidFindBy(id = "com.saucelabs.mydemoapp.android:id/alertTitle")
    private WebElement lblAlertTitle;

    @AndroidFindBy(id = "android:id/message")
    private WebElement lblAlertMessage;

    @AndroidFindBy(id = "android:id/button1")
    private WebElement btnPositive;

    @AndroidFindBy(id = "android:id/button2")
    private WebElement btnCancel;

    public boolean isConfirmationAlertDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertTitle)).isDisplayed()
                    && wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getConfirmationAlertMessage() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).getText();
    }

    public void confirmReset() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnPositive)).click();
    }

    public void cancelReset() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnCancel)).click();
    }

    public boolean isSuccessAlertDisplayed() {
        ensureElementsInitialized();
        try {
            return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessAlertMessage() {
        ensureElementsInitialized();
        return wait(existenceCheckTimeout()).until(ExpectedConditions.visibilityOf(lblAlertMessage)).getText();
    }

    public void dismissSuccessAlert() {
        ensureElementsInitialized();
        wait(shortWait()).until(ExpectedConditions.elementToBeClickable(btnPositive)).click();
    }
}